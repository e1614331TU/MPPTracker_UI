package base;
/**
* SoftwareVersionDAO
* <p>
* this class is needed to display the actual version of the controller in the config-panel
*/
public class SoftwareVersionDAO {

	private short minor = 0;
	private short major = 0;
	private short day = 0;
	private short month = 0;
	private short year = 0;
	
	public SoftwareVersionDAO() {
		
	}
	
	public void setMinorVersion(short minor) {
		this.minor = minor;
	}
	
	public void setMajorVersion(short major) {
		this.major = major;
	}
	
	public void setDay(short day) {
		this.day = day;
	}
	
	public void setMonth(short month) {
		this.month = month;
	}
	
	public void setYear(short year) {
		this.year = year;
	}
	
	@Override
	public String toString() {
		return "MPP v" + Short.toString(this.major) + "." + Short.toString(this.minor) + " (" + Short.toString(this.day) + "." + Short.toString(this.month) + "." + Short.toString(this.year) + ")";
	}
	
}
