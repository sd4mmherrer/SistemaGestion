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
 * Diálogo para buscar un producto por su ID.
 */
public class DialogoBuscar {

    public static void mostrar(ProductoServicio servicio) {
        Stage dialogo = new Stage();
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Buscar Producto por ID");
        dialogo.setResizable(false);

        VBox root = new VBox(14);
        root.getStyleClass().add("dialogo-root");
        root.setPadding(new Insets(24));
        root.setPrefWidth(360);

        Label titulo = new Label("Buscar Producto");
        titulo.getStyleClass().add("dialogo-titulo");

        Label lblId = new Label("ID del producto");
        lblId.getStyleClass().add("campo-label");

        TextField txtId = new TextField();
        txtId.setPromptText("Ej: 3");
        txtId.getStyleClass().add("campo-formulario");

        // Tarjeta de resultado
        VBox resultado = new VBox(8);
        resultado.getStyleClass().add("resultado-card");
        resultado.setVisible(false);

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("btn-primary");
        btnBuscar.setDefaultButton(true);
        btnBuscar.setOnAction(e -> {
            resultado.getChildren().clear();
            Producto p = servicio.buscarPorId(txtId.getText());
            if (p != null) {
                resultado.setVisible(true);
                resultado.getChildren().addAll(
                    crearFila("ID",       String.valueOf(p.getId())),
                    crearFila("Nombre",   p.getNombre()),
                    crearFila("Precio",   String.format("$%.2f", p.getPrecio())),
                    crearFila("Cantidad", String.valueOf(p.getCantidad()))
                );
            } else {
                resultado.setVisible(true);
                Label noEnc = new Label("No se encontró ningún producto con ese ID.");
                noEnc.getStyleClass().add("msg-error");
                resultado.getChildren().add(noEnc);
            }
        });

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.getStyleClass().add("btn-cancelar");
        btnCerrar.setOnAction(e -> dialogo.close());

        HBox botones = new HBox(10, btnBuscar, btnCerrar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titulo, lblId, txtId, botones, resultado);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            DialogoBuscar.class.getResource("/presentacion/estilos.css").toExternalForm()
        );

        dialogo.setScene(scene);
        dialogo.showAndWait();
    }

    private static HBox crearFila(String campo, String valor) {
        HBox fila = new HBox(10);
        Label lCampo = new Label(campo + ":");
        lCampo.getStyleClass().add("resultado-campo");
        lCampo.setMinWidth(70);
        Label lValor = new Label(valor);
        lValor.getStyleClass().add("resultado-valor");
        fila.getChildren().addAll(lCampo, lValor);
        return fila;
    }
}
