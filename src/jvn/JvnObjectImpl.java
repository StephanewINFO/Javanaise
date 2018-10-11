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

	public JvnObjectImpl(int id, Serializable obj){
		this.obj = obj;
		this.joi = id;
		this.etatVerrou = States.NL;
	}
	
	public void jvnLockRead() throws JvnException {
             
              switch(etatVerrou){
                  case NL:
                	  obj = JvnServerImpl.jvnGetServer().jvnLockRead(joi);    
                	  etatVerrou = States.R;
                      break;
     
                  case RC:
                      etatVerrou = States.R;
                      break;
                  case WC:
                	  etatVerrou = States.RWC;
                      break;
             
              }   
            
        }
		
	

	public void jvnLockWrite() throws JvnException {
		
			switch(etatVerrou) {
			case WC:
				etatVerrou = States.W;
				break;
			case NL:
				obj = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
				etatVerrou = States.W;
				break;
			case RC:
				obj = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
				etatVerrou = States.W;
				break;
			}
		
		
	}

	public void jvnUnLock() throws JvnException {
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
		
	}

	public int jvnGetObjectId() throws JvnException {
		return joi;
	}

	public Serializable jvnGetObjectState() throws JvnException {
		return obj;
	}


	public void jvnInvalidateReader() throws JvnException {
		
		switch(etatVerrou) {
		case RC:
			etatVerrou = States.NL;
			break;
		case RWC:
			etatVerrou = States.NL;
			break;
		case WC:
			etatVerrou = States.NL;
			break;
		case R:
			etatVerrou = States.NL;
			break;
			
		}
		
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		switch(etatVerrou) {
		case WC:
			etatVerrou = States.NL;
			break;
		case RC:
			etatVerrou = States.NL;
			break;
		case RWC:
			etatVerrou = States.NL;
			break;
		}
		return obj;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		switch(etatVerrou) {
		case WC:
			etatVerrou = States.RC;
			break;
		case W:
			etatVerrou = States.RC;
			break;
		case RWC:
			etatVerrou = States.R;
			break;
		}
		return obj;
		}
		
	}

       
    
