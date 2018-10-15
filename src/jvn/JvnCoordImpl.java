/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;



public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	private int id;
	private HashMap<String, JvnObject> objectNames;
	private HashMap<Integer, HashSet<JvnRemoteServer>> jvnObjects;
	private HashMap<Integer, HashSet<JvnRemoteServer>> readers;
	private HashMap<Integer, JvnRemoteServer> writer;



/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		objectNames = new HashMap<String, JvnObject>();
		jvnObjects = new HashMap<Integer, HashSet<JvnRemoteServer>>();
		readers = new HashMap<Integer, HashSet<JvnRemoteServer>>();
		writer = new HashMap<Integer, JvnRemoteServer>();
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    return id++;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	  if(!objectNames.containsKey(jon)) {
		  objectNames.put(jon, jo);
		  jvnObjects.put(jo.jvnGetObjectId(), new HashSet<JvnRemoteServer>());
		  jvnObjects.get(jo.jvnGetObjectId()).add(js);
		  readers.put(jo.jvnGetObjectId(), new HashSet<JvnRemoteServer>());
		  writer.put(jo.jvnGetObjectId(), js);
	  }else {
		  throw new JvnException("Error registering object");
	  }
      
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	  
	  JvnObject object = null;
	  
	  if(!objectNames.isEmpty()) {
		  if(objectNames.containsKey(jon)) {
			  object = objectNames.get(jon);
			  jvnObjects.get(object.jvnGetObjectId()).add(js);
		  }
	  }
	  return object;
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
  
	   Serializable objectUpdate = null;
	   try {
		   if(writer.get(joi) != null ) {			 			  
			   JvnRemoteServer writerJvnServer = writer.get(joi);
			   objectUpdate = writerJvnServer.jvnInvalidateWriterForReader(joi);
			   writer.put(joi, null);
			   readers.get(joi).add(writerJvnServer);
		   }
		   readers.get(joi).add(js);
	   }catch(Exception e) {
		   System.err.println("Error on coordinator at jvnLockRead():" + e) ;
			e.printStackTrace();
	   }
	   return objectUpdate;
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   Serializable objectUpdate = null;
	   try {
		   if(writer.containsKey(joi)) {
			   if(writer.get(joi) != null) {
				   JvnRemoteServer writerJvnServer = writer.get(joi);
				   objectUpdate = writerJvnServer.jvnInvalidateWriter(joi);
				   writer.put(joi, null);
			   }else {
				   if(!(readers.get(joi).isEmpty())) {
					   for(JvnRemoteServer s:readers.get(joi)) {
						   s.jvnInvalidateReader(joi); 
					   }
					   readers.get(joi).clear();
				   }
			   }
		   }
		   writer.put(joi, js);
		   
	   }catch(Exception e) {
		   System.err.println("Error on server at jvnLockWrite():" + e) ;
			e.printStackTrace();
	   }
    return objectUpdate;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
    	
////    	remove js with his jvnObjects from HashMap jvnObjects and objectNames
//    	if(jvnObjects.containsValue(js)) {
//    		for(int id:jvnObjects.keySet()) {
//    			if(jvnObjects.get(id).equals(js)) {
//    				for(String name:objectNames.keySet()) {
//    					HashSet<JvnObject> setObjects = objectNames.get(name);
//    						for(JvnObject obj:setObjects) {
//    							if(obj.jvnGetObjectId() == id) {
//    								setObjects.remove(obj);
//    							}
//    						}
//    				}
//    			}
//    			jvnObjects.remove(id);
//    			if (readers.containsKey(id)) {
//					readers.remove(id);
//				}
//    			if(writer.containsKey(id)) {
//    				writer.remove(id);
//    			}
//    		}
//    	}
    }
    
    
    public static void main(String[] args) {
    	JvnRemoteCoord h_stub;
		try {
			h_stub = new JvnCoordImpl();
			 Registry registry= LocateRegistry.getRegistry();
			 registry.rebind("Coord", h_stub);
			 System.out.println ("Server ready");
		} catch (Exception e)  {
			System.err.println("Error on server :" + e) ;
			e.printStackTrace();
		}

	}
}

 
