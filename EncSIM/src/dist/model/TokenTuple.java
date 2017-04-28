package dist.model;

public class TokenTuple {
	
	private byte[] token;
	private byte[] random;
	public byte[] getToken() {
		return token;
	}
	public void setToken(byte[] token) {
		this.token = token;
	}
	public byte[] getRandom() {
		return random;
	}
	public void setRandom(byte[] random) {
		this.random = random;
	}

}
