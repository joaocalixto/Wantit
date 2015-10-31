package neurotech.com.br.wantit.model;

public class App {

	private String name;
	private String bundleId;
	private String category;
	private String price;

	public App() {
		super();
	}

	public App(String name, String packageName, String category, String price) {
		super();
		this.name = name;
		this.bundleId = packageName;
		this.category = category;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return bundleId;
	}

	public void setPackageName(String packageName) {
		this.bundleId = packageName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
