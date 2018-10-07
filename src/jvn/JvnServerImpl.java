/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;

import irc.Sentence;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;




public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	
  // A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
        private JvnRemoteCoord jrc;
        private HashSet<JvnObject> mapObject;
  /**
  * Default constructor
  * @throws JvnException
  **/
	JvnServerImpl() throws Exception {
		super();
		this.mapObject = new HashSet<JvnObject>();
       try {
         
                Registry registry = LocateRegistry.getRegistry();
                jrc = (JvnRemoteCoord) registry.lookup("Coord");
                System.err.println("ServerLocal connected: " + this.hashCode());

             } catch (Exception e) {
			        System.err.println("Error on client JvnServerImpl(): " + e);
			        e.printStackTrace();
			}
}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				System.err.println("ServerLocal jvnGetServer: ");
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
            try {
                jrc.jvnTerminate(js);
                
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
	} 
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException {
		try {
			JvnObject jvnObject = new JvnObjectImpl(o);
			this.mapObject.add(jvnObject);
			return jvnObject;
		}catch(Exception e) {
			System.err.println("Error jvnCreateObject(): " + e);
	        e.printStackTrace();
	        return null;
		}	
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
            try {
                jrc.jvnRegisterObject(jon, jo, js);
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	/**
	* Provide the reference of a JVN object being given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
          	JvnObject object;
            try {
            	object =  jrc.jvnLookupObject(jon, js);
            	return object;
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            
           
        }
            
            //buscar en la lista local y su estado NL,RLC
            //sino existe busca en el coord y retorna el objeto
    // to be completed 
	
	
	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockRead(int joi)
	 throws JvnException {
            try {
                return jrc.jvnLockRead(joi, js);
                
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        		return null;
            }
		

	}	
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
	   try {
           return jrc.jvnLockWrite(joi, js);
           
       } catch (RemoteException ex) {
           Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
           return null;
       }
		
	}	

	//Sba:Methodes RemoteServer
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
	  	for(JvnObject jo:this.mapObject) {
	  		try {
	  			if(jo.jvnGetObjectId() == joi) {
		  			jo.jvnInvalidateReader();
		  		}
	  		}catch(Exception e) {
	  			throw new JvnException(e.getMessage());
	  		}
	  		
	  	}
	};
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException { 
	  Serializable result = null;
	  for(JvnObject jo:this.mapObject) {
	  		try {
	  			if(jo.jvnGetObjectId() == joi) {
		  			result = jo.jvnInvalidateWriter();
		  		}
	  		}catch(Exception e) {
	  			throw new JvnException(e.getMessage());
	  		}
	  		
	  	}
	  return result;
	};
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException { 
	   Serializable result = null;
		  for(JvnObject jo:this.mapObject) {
		  		try {
		  			if(jo.jvnGetObjectId() == joi) {
			  			result = jo.jvnInvalidateWriterForReader();
			  		}
		  		}catch(Exception e) {
		  			throw new JvnException(e.getMessage());
		  		}
		  		
		  	}
		  return result;
	 };

}

 
