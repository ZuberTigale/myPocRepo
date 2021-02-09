package com.demo.model;

import java.util.Set;

public class AuthToken {
	 private String token;
	    private String username;
	    private Set authority;

	    public AuthToken(){

	    }

	    public AuthToken(String token, String username, Set authority){
	        this.token = token;
	        this.username = username;
	        this.authority = authority;
	    }
	    

		public Set getAuthority() {
			return authority;
		}

		public void setAuthority(Set authority) {
			this.authority = authority;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

}
