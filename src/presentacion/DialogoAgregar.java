package presentacion;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logica.ProductoServicio;

/**
 * Diálogo para agregar un nuevo producto.
 * Sigue siendo capa de Presentación — no valida reglas de negocio.
 */
public class DialogoAgregar {

    public static void mostrar(ProductoServicio servicio, Runnable alGuardar) {
        Stage dialogo = new Stage();
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Agregar Nuevo Producto");
        dialogo.setResizable(false);

        VBox root = new VBox(16);
        root.getStyleClass().add("dialogo-root");
        root.setPadding(new Insets(24));
        root.setPrefWidth(380);

        Label titulo = new Label("Nuevo Producto");
        titulo.getStyleClass().add("dialogo-titulo");

        // ── Campos del formulario
        TextField txtNombre   = crearCampo("Nombre del producto", "Ej: Laptop Dell XPS");
        TextField txtPrecio   = crearCampo("Precio", "Ej: 850.99");
        TextField txtCantidad = crearCampo("Cantidad en stock", "Ej: 50");

        Label lblMensaje = new Label("");
        lblMensaje.getStyleClass().add("dialogo-mensaje");
        lblMensaje.setWrapText(true);

        // ── Botones
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-cancelar");
        btnCancelar.setOnAction(e -> dialogo.close());

        Button btnGuardar = new Button("Guardar Producto");
        btnGuardar.getStyleClass().add("btn-primary");
        btnGuardar.setDefaultButton(true);
        btnGuardar.setOnAction(e -> {
            String resultado = servicio.agregarProducto(
                txtNombre.getText(),
                txtPrecio.getText(),
                txtCantidad.getText()
            );

            if (resultado.startsWith("Error")) {
                lblMensaje.getStyleClass().setAll("dialogo-mensaje", "msg-error");
                lblMensaje.setText(resultado);
            } else {
                lblMensaje.getStyleClass().setAll("dialogo-mensaje", "msg-exito");
                lblMensaje.setText(resultado);
                alGuardar.run();
                // Cerrar después de un breve momento visual de éxito
                new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1)
                ).play();
                dialogo.close();
            }
        });

        botones.getChildren().addAll(btnCancelar, btnGuardar);

        root.getChildren().addAll(
            titulo,
            crearEtiquetaCampo("Nombre"), txtNombre,
            crearEtiquetaCampo("Precio ($)"), txtPrecio,
            crearEtiquetaCampo("Cantidad"), txtCantidad,
            lblMensaje,
            botones
        );

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            DialogoAgregar.class.getResource("/presentacion/estilos.css").toExternalForm()
        );

        dialogo.setScene(scene);
        dialogo.showAndWait();
    }

    private static TextField crearCampo(String prompt, String hint) {
        TextField tf = new TextField();
        tf.setPromptText(hint);
        tf.getStyleClass().add("campo-formulario");
        return tf;
    }

    private static Label crearEtiquetaCampo(String texto) {
        Label lbl = new Label(texto);
        lbl.getStyleClass().add("campo-label");
        VBox.setMargin(lbl, new Insets(4, 0, 0, 0));
        return lbl;
    }
}
