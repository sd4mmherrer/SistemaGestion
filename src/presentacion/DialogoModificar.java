package presentacion;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entidades.Producto;
import logica.ProductoServicio;

/**
 * Diálogo para modificar un producto existente.
 * Precarga los datos actuales del producto seleccionado.
 */
public class DialogoModificar {

    public static void mostrar(ProductoServicio servicio, Producto producto, Runnable alGuardar) {
        Stage dialogo = new Stage();
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Modificar Producto — ID: " + producto.getId());
        dialogo.setResizable(false);

        VBox root = new VBox(16);
        root.getStyleClass().add("dialogo-root");
        root.setPadding(new Insets(24));
        root.setPrefWidth(380);

        Label titulo = new Label("Modificar Producto #" + producto.getId());
        titulo.getStyleClass().add("dialogo-titulo");

        // Pre-cargamos los valores actuales
        TextField txtNombre   = new TextField(producto.getNombre());
        TextField txtPrecio   = new TextField(String.valueOf(producto.getPrecio()));
        TextField txtCantidad = new TextField(String.valueOf(producto.getCantidad()));

        txtNombre.getStyleClass().add("campo-formulario");
        txtPrecio.getStyleClass().add("campo-formulario");
        txtCantidad.getStyleClass().add("campo-formulario");

        Label lblMensaje = new Label("");
        lblMensaje.getStyleClass().add("dialogo-mensaje");
        lblMensaje.setWrapText(true);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-cancelar");
        btnCancelar.setOnAction(e -> dialogo.close());

        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.getStyleClass().add("btn-primary");
        btnGuardar.setDefaultButton(true);
        btnGuardar.setOnAction(e -> {
            String resultado = servicio.modificarProducto(
                String.valueOf(producto.getId()),
                txtNombre.getText(),
                txtPrecio.getText(),
                txtCantidad.getText()
            );

            if (resultado.startsWith("Error")) {
                lblMensaje.getStyleClass().setAll("dialogo-mensaje", "msg-error");
                lblMensaje.setText(resultado);
            } else {
                alGuardar.run();
                dialogo.close();
            }
        });

        botones.getChildren().addAll(btnCancelar, btnGuardar);

        root.getChildren().addAll(
            titulo,
            crearLabel("Nombre"), txtNombre,
            crearLabel("Precio ($)"), txtPrecio,
            crearLabel("Cantidad"), txtCantidad,
            lblMensaje,
            botones
        );

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            DialogoModificar.class.getResource("/presentacion/estilos.css").toExternalForm()
        );

        dialogo.setScene(scene);
        dialogo.showAndWait();
    }

    private static Label crearLabel(String texto) {
        Label lbl = new Label(texto);
        lbl.getStyleClass().add("campo-label");
        VBox.setMargin(lbl, new Insets(4, 0, 0, 0));
        return lbl;
    }
}
