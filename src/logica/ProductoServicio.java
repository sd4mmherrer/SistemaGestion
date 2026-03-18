package logica;

import accesodatos.ProductoDAO;
import entidades.Producto;
import java.util.List;

public class ProductoServicio {
    private ProductoDAO dao = new ProductoDAO();

    public String agregarProducto(String nombre, String precioTxt, String cantTxt) {
        if (nombre == null || nombre.trim().isEmpty())
            return "Error: El nombre no puede estar vacío.";
        if (precioTxt == null || precioTxt.trim().isEmpty())
            return "Error: El precio no puede estar vacío.";
        if (cantTxt == null || cantTxt.trim().isEmpty())
            return "Error: La cantidad no puede estar vacía.";

        double precio;
        int cantidad;
        try { precio = Double.parseDouble(precioTxt.trim()); }
        catch (NumberFormatException e) { return "Error: El precio debe ser un número válido (ej: 19.99)."; }
        try { cantidad = Integer.parseInt(cantTxt.trim()); }
        catch (NumberFormatException e) { return "Error: La cantidad debe ser un número entero."; }

        if (precio < 0) return "Error: El precio no puede ser negativo.";
        if (cantidad < 0) return "Error: La cantidad no puede ser negativa.";

        List<Producto> lista = dao.leerTodos();
        int nuevoId = generarNuevoId(lista);
        lista.add(new Producto(nuevoId, nombre.trim(), precio, cantidad));
        return dao.guardarTodos(lista) ? "Producto agregado con ID: " + nuevoId : "Error: No se pudo guardar.";
    }

    public List<Producto> listarProductos() { return dao.leerTodos(); }

    public Producto buscarPorId(String idTxt) {
        if (idTxt == null || idTxt.trim().isEmpty()) return null;
        int id;
        try { id = Integer.parseInt(idTxt.trim()); } catch (NumberFormatException e) { return null; }
        for (Producto p : dao.leerTodos()) if (p.getId() == id) return p;
        return null;
    }

    public String modificarProducto(String idTxt, String nombre, String precioTxt, String cantTxt) {
        int id;
        try { id = Integer.parseInt(idTxt.trim()); }
        catch (NumberFormatException e) { return "Error: El ID debe ser un número entero."; }

        if (nombre == null || nombre.trim().isEmpty()) return "Error: El nombre no puede estar vacío.";

        double precio;
        int cantidad;
        try { precio = Double.parseDouble(precioTxt.trim()); }
        catch (NumberFormatException e) { return "Error: El precio debe ser un número válido."; }
        try { cantidad = Integer.parseInt(cantTxt.trim()); }
        catch (NumberFormatException e) { return "Error: La cantidad debe ser un entero."; }

        if (precio < 0) return "Error: El precio no puede ser negativo.";
        if (cantidad < 0) return "Error: La cantidad no puede ser negativa.";

        List<Producto> lista = dao.leerTodos();
        boolean encontrado = false;
        for (Producto p : lista) {
            if (p.getId() == id) {
                p.setNombre(nombre.trim()); p.setPrecio(precio); p.setCantidad(cantidad);
                encontrado = true; break;
            }
        }
        if (!encontrado) return "Error: No se encontró un producto con ID " + id + ".";
        return dao.guardarTodos(lista) ? "Producto con ID " + id + " modificado." : "Error al guardar.";
    }

    public String eliminarProducto(String idTxt) {
        int id;
        try { id = Integer.parseInt(idTxt.trim()); }
        catch (NumberFormatException e) { return "Error: El ID debe ser un entero."; }

        List<Producto> lista = dao.leerTodos();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) { lista.remove(i); encontrado = true; break; }
        }
        if (!encontrado) return "Error: No se encontró un producto con ID " + id + ".";
        return dao.guardarTodos(lista) ? "Producto con ID " + id + " eliminado." : "Error al guardar.";
    }

    private int generarNuevoId(List<Producto> lista) {
        int max = 0;
        for (Producto p : lista) if (p.getId() > max) max = p.getId();
        return max + 1;
    }
}
