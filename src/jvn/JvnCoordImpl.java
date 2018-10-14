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
	private HashMap<String, HashSet<JvnObject>> objectNames;
	private HashMap<Integer, JvnRemoteServer> jvnObjects;
	private HashMap<Integer, JvnRemoteServer> readers;
	private HashMap<Integer, JvnRemoteServer> writer;
	private Serializable objectUpdate;



/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		objectNames = new HashMap<String, HashSet<JvnObject>>();
		jvnObjects = new HashMap<Integer, JvnRemoteServer>();
		readers = new HashMap<Integer, JvnRemoteServer>();
		writer = new HashMap<Integer, JvnRemoteServer>();
		objectUpdate = null;
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
	  if(objectNames.containsKey(jon)) {
		  HashSet<JvnObject> objs = objectNames.get(jon);
		  if(objs == null) {
			  objs = new HashSet<JvnObject>();
		  }
		  objs.add(jo);
	  }
      jvnObjects.put(jo.jvnGetObjectId(), js);
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
			  for(JvnObject o:objectNames.get(jon)) {
				  for(int id:jvnObjects.keySet()) {
					  if(jvnObjects.get(id) == js) {
						  object = o;
					  }
				  }
			  }
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
   
	   System.out.println(joi + " do jvnLockReaad");
	   try {
		   if(!writer.isEmpty()) {
			   int writerId = 0;
			   JvnRemoteServer writerJvnServer = null;
			   for(int id:writer.keySet()) {
				   writerId = id;
				   writerJvnServer = writer.get(id);
			   }
			   objectUpdate = writerJvnServer.jvnInvalidateWriterForReader(writerId);
                           
			   writer.clear();
			  // readers.put(writerId, writerJvnServer);
		   }
		   readers.put(joi, js);
	   }catch(Exception e) {
		   System.err.println("Error on coordinator at jvnLockRead():" + e) ;
			e.printStackTrace();
	   }
	   System.out.println("writer :" + writer.toString());
	   System.out.println("readers :" + readers.toString());
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
	   System.out.println(joi + " do jvnLockWrite");
	   try {
		   if(!writer.isEmpty()) {
			   int writerId = 0;
			   JvnRemoteServer writerJvnServer = null;
			   for(int id:writer.keySet()) {
				   writerId = id;
				   writerJvnServer = writer.get(id);
			   }
			   objectUpdate = writerJvnServer.jvnInvalidateWriter(writerId);
			   writer.clear();
		   }
		   if(writer.isEmpty() && !readers.isEmpty()) {
			   JvnRemoteServer readerJvnServer = null;
			   for(int id:readers.keySet()) {
				   readerJvnServer = readers.get(id);
				   readerJvnServer.jvnInvalidateReader(id);
			   }
			   readers.clear();
		   }
		   writer.put(joi, js);
		   
	   }catch(Exception e) {
		   System.err.println("Error on server at jvnLockWrite():" + e) ;
			e.printStackTrace();
	   }
//	   System.out.println("writer :" + writer.toString());
//	   System.out.println("readers :" + readers.toString());
    return objectUpdate;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
    	
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

 
