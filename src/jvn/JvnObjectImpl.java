package jvn;

import java.io.Serializable;

import irc.Sentence;


public class JvnObjectImpl implements JvnObject{

        
	public Serializable obj;
	public int joi;
	enum States 
        {
		NL,RC, WC,R,W,RWC
	}
        
        States etatVerrou;
	
	public States getEtatVerrou() {
			return etatVerrou;
		}

		public void setEtatVerrou(States etatVerrou) {
			this.etatVerrou = etatVerrou;
		}

	public JvnObjectImpl(Serializable obj){
		this.obj=obj;
		joi=this.hashCode();
		this.etatVerrou = States.W;
//		try {
//			this.jvnLockWrite();
//		}catch(Exception e) {
//			System.err.println(e);
//			e.printStackTrace();
//		}
		
	}
	
	public void jvnLockRead() throws JvnException {
              JvnServerImpl js = JvnServerImpl.jvnGetServer();
              
              switch(etatVerrou){
                  case NL:
                	  obj = js.jvnLockRead(joi);    
                	  etatVerrou = States.R;
                      break;
     
                  case RC:
                	  obj = js.jvnLockRead(joi);
                      etatVerrou = States.R;
                      break;
                  case WC:
                	  etatVerrou = States.RWC;
                      break;
             
              }   
              
              
            
                   
            
        }
		// TODO Auto-generated method stub
		
	

	public void jvnLockWrite() throws JvnException {
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		switch(etatVerrou) {
		case WC:
			etatVerrou = States.W;
			break;
		case NL:
			obj = js.jvnLockWrite(joi);
			etatVerrou = States.W;
			break;
		}
		
	}

	public void jvnUnLock() throws JvnException {
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		switch(etatVerrou) {
		case R:
			etatVerrou = States.RC;
			break;
		case W:
			etatVerrou = States.WC;
			break;	
		case RWC:
			etatVerrou = States.WC;
			break;
		default:
			break;
		}
		//notify();
		
	}

	public int jvnGetObjectId() throws JvnException {
		return this.joi;
	}
//completar
	public Serializable jvnGetObjectState() throws JvnException {
		return this.obj;
	}


	public void jvnInvalidateReader() throws JvnException {
		
		switch(this.etatVerrou) {
		case RC:
			this.etatVerrou = States.NL;
			break;
		case RWC:
			try {
				wait();
				this.etatVerrou = States.NL;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
}
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		switch(this.etatVerrou) {
		case WC:
			this.etatVerrou = States.NL;
			break;
		case W:
		case RWC:
			try {
				wait();
				this.etatVerrou = States.NL;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
		return obj;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		switch(this.etatVerrou) {
		case WC:
			this.etatVerrou = States.RC;
			break;
		case W:
			try {
				wait();
				this.etatVerrou = States.RC;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case RWC:
			try {
				wait();
				this.etatVerrou = States.R;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			return obj;
		}
	}

       
    
