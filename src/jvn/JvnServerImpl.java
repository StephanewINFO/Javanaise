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
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;




public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	
  // A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
        private JvnRemoteCoord jrc=null;
        public  HashMap<String,JvnObject> mapObject;
  /**
  * Default constructor
  * @throws JvnException
  **/
	JvnServerImpl() throws Exception {
		super();
		this.mapObject =new HashMap<>();
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
                
                // to be completed 
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
        
            int joi=0;
            try {
                joi = jrc.jvnGetObjectId();
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            JvnObjectImpl jo=new JvnObjectImpl(joi,o);
            return jo; 
                
	}

	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
            mapObject.put(jon, jo);
            try {
                jrc.jvnRegisterObject(jon, jo, js);
                // to be completed 
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	/**
	* Provide the reference of a JVN object beeing given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
          
            
            for (Map.Entry<String, JvnObject> entry : mapObject.entrySet()) {
                if (entry.getKey().equals(jon)){
                    return entry.getValue();
                } 
            }
            try {
                return jrc.jvnLookupObject(jon, js);
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
           
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
                jrc.jvnLockRead(joi, js);
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
		// to be completed 
		return null;

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
                jrc.jvnLockWrite(joi, js);
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
		return null;
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
				// to be completed 
                JvnObjectImpl jo;
            for (Map.Entry<String, JvnObject> entry : mapObject.entrySet()) {    
                  jo=(JvnObjectImpl) entry.getValue();
                   
                if(jo.jvnGetObjectId()==joi){
                      
                      try {
                          jo.jvnInvalidateReader();
                          
                          jo.wait();
                          
                          
                      } catch (InterruptedException ex) {
                          Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                      }
                       
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
		// to be completed 
                JvnObjectImpl jo;
            for (Map.Entry<String, JvnObject> entry : mapObject.entrySet()) {    
                  jo=(JvnObjectImpl) entry.getValue();
                   
                if(jo.jvnGetObjectId()==joi){
                      
                      try {
                          jo.jvnInvalidateWriter();
                          
                          jo.wait();
                          
                          return jo;
                      } catch (InterruptedException ex) {
                          Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                      }
                       
                }   
                
            }
                
		return null;
	
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed    
            JvnObjectImpl jo;
            for (Map.Entry<String, JvnObject> entry : mapObject.entrySet()) {    
                  jo=(JvnObjectImpl) entry.getValue();
                   
                if(jo.jvnGetObjectId()==joi){
                      
                      try {
                          jo.jvnInvalidateWriterForReader();
                          
                          jo.wait();
                          
                          return jo;
                      } catch (InterruptedException ex) {
                          Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                      }
                       
                }   
                
            }
                
		return null;
	

}

 

}
