/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;




public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	
  // A JVN server is managed as a singleton 
	private HashMap<Integer, JvnObject> mapJvnObject;
	private static JvnServerImpl js = null;
        private JvnRemoteCoord jrc;
  /**
  * Default constructor
  * @throws JvnException
  **/
	JvnServerImpl() throws Exception {
		super();
		mapJvnObject = new HashMap<Integer, JvnObject>();
       try {
         
                Registry registry = LocateRegistry.getRegistry();
                jrc = (JvnRemoteCoord) registry.lookup("Coord");
                System.err.println("ServerLocal connected: " + jrc.hashCode());

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
				System.err.println("ServerLocal jvnGetServer: "+js);
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
			int id = jrc.jvnGetObjectId();
			JvnObject jvnObject = new JvnObjectImpl(id, o);
			mapJvnObject.put(id, jvnObject);
			jvnObject.jvnLockWrite();
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
          	JvnObject object = null;
            try {
            	object =  jrc.jvnLookupObject(jon, js);
//            	if(object != null) {
//            		JvnObject newJvnObj = new JvnObjectImpl(jrc.jvnGetObjectId(), object.jvnGetObjectState());
//                	jrc.jvnRegisterObject(jon, newJvnObj, js);
//                	mapJvnObject.put(newJvnObj.jvnGetObjectId(), newJvnObj);
//                	object = newJvnObj;
//            	}
            	
            	
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
			return object;
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
	   Serializable result;
            try {
                result = jrc.jvnLockRead(joi, js);
                if(result == null) {
                	result = mapJvnObject.get(joi).jvnGetObjectState();
                }
                
            } catch (RemoteException ex) {
                Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        		return null;
            }
		return result;

	}	
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
	   Serializable result;
	   try {
           result = jrc.jvnLockWrite(joi, js);
           if(result == null) {
           	result = mapJvnObject.get(joi).jvnGetObjectState();
           }
           
       } catch (RemoteException ex) {
           Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
           return null;
       }
	   return result;
		
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
	  try {
		  for(int id:mapJvnObject.keySet()) {
			  if(id == joi) {
				  mapJvnObject.get(id).jvnInvalidateReader();
			  }
		  }
	  }
	  catch(Exception e) {
			throw new JvnException(e.getMessage());
	  }
	  	
 }
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException { 
	  Serializable result = null;
	  try {
		  for(int id:mapJvnObject.keySet()) {
			  if(id == joi) {
				  result = mapJvnObject.get(id).jvnInvalidateWriter();
			  }
		  }
	  }
	  catch(Exception e) {
			throw new JvnException(e.getMessage());
	  }
	  return result;
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException { 
	   Serializable result = null;
	   try {
			  for(int id:mapJvnObject.keySet()) {
				  if(id == joi) {
					  result = mapJvnObject.get(id).jvnInvalidateWriterForReader();
				  }
			  }
		  }
		  catch(Exception e) {
				throw new JvnException(e.getMessage());
		  }
		  return result;
	 }

}

 
