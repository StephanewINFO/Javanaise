/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvn;

public class Triplet {

    private String name;
    private JvnObject jvnObject;
    private JvnRemoteServer jvnRemoteServer;

    public Triplet(String jon, JvnObject jo, JvnRemoteServer js) {
        this.name = jon;
        this.jvnObject = jo;
        this.jvnRemoteServer = js;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JvnObject getJvnObject() {
		return jvnObject;
	}

	public void setJvnObject(JvnObject jvnObject) {
		this.jvnObject = jvnObject;
	}

	public JvnRemoteServer getJvnRemoteServer() {
		return jvnRemoteServer;
	}

	public void setJvnRemoteServer(JvnRemoteServer jvnRemoteServer) {
		this.jvnRemoteServer = jvnRemoteServer;
	}

    
}