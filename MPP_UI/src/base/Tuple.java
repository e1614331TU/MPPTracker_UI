package base;

public class Tuple<T,V> {
	private T valA;
	private V valB;
	
	public Tuple(T valA, V valB) {
		this.valA = valA;
		this.valB = valB;
	}
	
	public T getValA() {
		return this.valA;
	}
	
	public V getValB() {
		return this.valB;
	}
	
	public void setValA(T valA) {
		this.valA = valA;
	}
	
	public void setValB(V valB) {
		this.valB = valB;
	}
}
