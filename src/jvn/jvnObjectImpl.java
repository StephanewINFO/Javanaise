package jvn;

import java.io.Serializable;

import irc.Sentence;


public class jvnObjectImpl implements JvnObject{

        
	public Serializable obj;
        public int joi;
       // enum etatVerrrou=null;
	enum States 
        {
		NL,RC, WC,R,W,RWC
	}
        
        States etatVerrou;
	
	public jvnObjectImpl(Serializable obj){
		this.obj=obj;
		joi=obj.hashCode();
		
	}
	
	public void jvnLockRead() throws JvnException {
              JvnServerImpl js = JvnServerImpl.jvnGetServer();
              
              switch(etatVerrou){
                  case NL:
                      
                      break;
                      
                  case RC:
                      obj=js.jvnLockRead(joi);
                      
                      break;
                  case R:
                      break;
                  
              }
              System.out.println("jvn.jvnObjectImpl.jvnLockRead()");     
              
              
            
                   
            
        }
		// TODO Auto-generated method stub
		
	

	public void jvnLockWrite() throws JvnException {
	
		// TODO Auto-generated method stub
		
	}

	public void jvnUnLock() throws JvnException {
		// TODO Auto-generated method stub
                etatVerrou =States.NL;
		
	}

	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
		return 0;
	}
//completar
	public Serializable jvnGetObjectState() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}


	public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub
		
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

       
        
}
