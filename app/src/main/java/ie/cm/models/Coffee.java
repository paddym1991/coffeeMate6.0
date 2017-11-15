package ie.cm.models;

public class Coffee
{
	public String name;
	public String shop;
	public double rating;
	public double price;
	public boolean favourite;

	public String _id;
	public String usertoken;
	public String address;
	public String googlephoto;
	public Marker marker = new Marker();


	public Coffee() {
		this.name = "";
		this.shop = "";
		this.rating = 0;
		this.price = 0;
		this.favourite = false;
		this.usertoken = "";
		this.address = "";
		this.marker.coords.latitude = 0;
		this.marker.coords.longitude = 0;
		this.googlephoto = null;
	}

	public Coffee(String name, String shop, double rating, double price, boolean fav, String token, double lat, double lng, String path)
	{
		this.name = name;
		this.shop = shop;
		this.rating = rating;
		this.price = price;
		this.favourite = fav;

		this.usertoken = token;
		this.address = "";
		this.marker.coords.latitude = lat;
		this.marker.coords.longitude = lng;
		this.googlephoto = path;
	}

	@Override
	public String toString() {
		return "Coffee [name=" + name
				+ ", shop =" + shop + ", rating=" + rating + ", price=" + price
				+ ", fav =" + favourite + " "
				+ usertoken + " " + address + " " + marker.coords.latitude + " " + marker.coords.longitude + "]";
	}
}
