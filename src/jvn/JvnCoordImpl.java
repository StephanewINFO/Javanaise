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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jvn.JvnObjectImpl.States;




public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	
//public  HashMap<String,JvnObject> mapObjnJo;
//public  HashMap<String,JvnRemoteServer> mapObjnJs;
public List<Triplet> listObjLocalServer;
//public Triplet <String, Integer, Integer> listObjLocalServer;



/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
           //  mapObjnJo= new HashMap<>();
            // mapObjnJs= new HashMap<>();
             listObjLocalServer = new ArrayList<>();
		// to be completed
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    // to be completed 
    int id=0;
    id++;
    return id;
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
      
         listObjLocalServer.add(new Triplet(jon, jo, js));
      
    // to be completed 
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
      
   Triplet ObjJs;
      
    for (int i = 0; i < listObjLocalServer.size(); i++) {        
        ObjJs = listObjLocalServer.get(i);
        
        if(ObjJs.getName().equals(jon) && ObjJs.getJvnRemoteServer().equals(js)){
            return ObjJs.getJvnObject();
        }
    }
    // to be completed 
    return null;
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
    // to be completed
    Triplet ObjJs;
       JvnObjectImpl jo;
       JvnRemoteServer jsRemote;
      
    for (int i = 0; i < listObjLocalServer.size(); i++) {        
        ObjJs = listObjLocalServer.get(i);
        jo=(JvnObjectImpl) ObjJs.getJvnObject();
        jsRemote= ObjJs.getJvnRemoteServer();
        
        if(jo.jvnGetObjectId()==joi && jsRemote.equals(js)){
            //cambio en el coord
            jo.setEtatVerrou(JvnObjectImpl.States.R);
            return jo;
        }
        //case 3
        if ((jo.jvnGetObjectId()==joi) && !(jsRemote.equals(js))){
            if (jo.getEtatVerrou().equals(States.R)) {
                listObjLocalServer.add(new Triplet("IRC", jo, js));
                return jo;
            }
        }
        
        if ((jo.jvnGetObjectId()==joi) && !(ObjJs.getJvnRemoteServer().equals(js))){
            if (jo.getEtatVerrou().equals(States.W)) {
                
            // retorna el objeto
              jsRemote.jvnInvalidateWriterForReader(joi);
              
              
              jo.setEtatVerrou(JvnObjectImpl.States.R);
              
              listObjLocalServer.add(new Triplet("IRC", jo, js));
              return jo;
            
            }
            
        }
   }
    return null;
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
       Triplet ObjJs;
       JvnObjectImpl jo;
       JvnRemoteServer jsRemote;
      
    for (int i = 0; i < listObjLocalServer.size(); i++) {        
        ObjJs = listObjLocalServer.get(i);
        jo=(JvnObjectImpl) ObjJs.getJvnObject();
        jsRemote= ObjJs.getJvnRemoteServer();        
        
        
        if ((jo.jvnGetObjectId()==joi) && !(jsRemote.equals(js))){
            if (jo.getEtatVerrou().equals(States.W)) {
            jsRemote.jvnInvalidateWriter(joi);
            listObjLocalServer.add(new Triplet("IRC", jo, js));
            return jo;     
            }
        }
            
        if ((jo.jvnGetObjectId()==joi) && !(jsRemote.equals(js))){
            if (jo.getEtatVerrou().equals(States.R)) {
            jsRemote.jvnInvalidateReader(joi);
            jo.setEtatVerrou(States.W);
            listObjLocalServer.add(new Triplet("IRC", jo, js));
            return jo;     
            }   
            
      
            
            
            
            }
        }
    // to be completed
    return null;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
           Triplet ObjJs;
      
    for (int i = 0; i < listObjLocalServer.size(); i++) {        
        ObjJs = listObjLocalServer.get(i);
        
        if( ObjJs.getJvnRemoteServer().equals(js)){
           listObjLocalServer.remove(ObjJs);
        }
    }
	 // to be completed
    }
    
    
    public static void main(String[] args) {
    	JvnRemoteCoord h_stub;
		try {
			h_stub = new JvnCoordImpl();
			//Registry registry = LocateRegistry.createRegistry(2500);
                        Registry registry= LocateRegistry.getRegistry();
			 registry.bind("Coord", h_stub);
			 System.out.println ("Server ready");
		} catch (Exception e)  {
		System.err.println("Error on server :" + e) ;
		e.printStackTrace();
		}

	}
}

 
