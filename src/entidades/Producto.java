package entidades;

public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int cantidad;

    public Producto() {}

    public Producto(int id, String nombre, double precio, int cantidad) {
        this.id = id; this.nombre = nombre;
        this.precio = precio; this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String aLinea() {
        return id + "|" + nombre + "|" + precio + "|" + cantidad;
    }

    public static Producto desdeLinea(String linea) {
        String[] p = linea.split("\\|");
        return new Producto(
            Integer.parseInt(p[0].trim()),
            p[1].trim(),
            Double.parseDouble(p[2].trim()),
            Integer.parseInt(p[3].trim())
        );
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Nombre: " + nombre +
               " | Precio: $" + precio + " | Cantidad: " + cantidad;
    }
}
