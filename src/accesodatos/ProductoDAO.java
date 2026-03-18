package accesodatos;

import entidades.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private static final String RUTA_ARCHIVO = "datos/productos.txt";

    public List<Producto> leerTodos() {
        List<Producto> lista = new ArrayList<>();
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    try { lista.add(Producto.desdeLinea(linea)); }
                    catch (Exception e) { System.out.println("[Aviso] Linea invalida omitida: " + linea); }
                }
            }
        } catch (IOException e) {
            System.out.println("[Error] No se pudo leer el archivo: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardarTodos(List<Producto> lista) {
        File carpeta = new File("datos");
        if (!carpeta.exists()) carpeta.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Producto p : lista) { writer.write(p.aLinea()); writer.newLine(); }
            return true;
        } catch (IOException e) {
            System.out.println("[Error] No se pudo guardar: " + e.getMessage());
            return false;
        }
    }
}
