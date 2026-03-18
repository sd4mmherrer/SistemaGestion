package presentacion;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entidades.Producto;
import logica.ProductoServicio;

import java.util.List;

/**
 * Capa de Presentación (Interfaz Gráfica).
 * Usa JavaFX para mostrar una UI con tema oscuro estilo dashboard.
 * NO tiene lógica de negocio ni acceso a archivos.
 */
public class Main extends Application {

    private ProductoServicio servicio;
    private TableView<Producto> tabla;
    private ObservableList<Producto> listaObservable;

    // ─── Etiquetas de estadísticas (se actualizan en tiempo real)
    private Label lblTotalProductos;
    private Label lblValorInventario;
    private Label lblStockBajo;

    @Override
    public void start(Stage stage) {
        servicio = new ProductoServicio();

        stage.setTitle("GestorPro — Sistema de Gestión de Productos");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // ─── Layout principal: sidebar + contenido
        BorderPane root = new BorderPane();
        root.setLeft(crearSidebar(stage));
        root.setCenter(crearAreaPrincipal());

        // ─── Aplicamos el CSS oscuro personalizado
        Scene scene = new Scene(root, 960, 620);
        scene.getStylesheets().add(
            getClass().getResource("/presentacion/estilos.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();

        // Cargamos los datos iniciales
        actualizarTabla();
    }

    // ═══════════════════════════════════════════════════════
    // SIDEBAR
    // ═══════════════════════════════════════════════════════

    private VBox crearSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);

        // Logo / título
        VBox logoBox = new VBox(6);
        logoBox.getStyleClass().add("sidebar-logo");
        logoBox.setAlignment(Pos.CENTER);

        Label icono = new Label("📦");
        icono.getStyleClass().add("sidebar-icon");

        Label titulo = new Label("GESTOR PRO");
        titulo.getStyleClass().add("sidebar-title");

        Label subtitulo = new Label("Sistema de Productos");
        subtitulo.getStyleClass().add("sidebar-subtitle");

        logoBox.getChildren().addAll(icono, titulo, subtitulo);

        // Separador
        Separator sep = new Separator();
        sep.getStyleClass().add("sidebar-sep");

        // Botones de navegación
        Button btnListar   = crearNavBtn("🗂  Ver Productos", true);
        Button btnAgregar  = crearNavBtn("➕  Agregar Nuevo", false);
        Button btnBuscar   = crearNavBtn("🔍  Buscar", false);
        Button btnModificar = crearNavBtn("✏️  Modificar", false);
        Button btnEliminar = crearNavBtn("🗑  Eliminar", false);

        // Acciones de cada botón
        btnListar.setOnAction(e -> actualizarTabla());
        btnAgregar.setOnAction(e -> DialogoAgregar.mostrar(servicio, this::actualizarTabla));
        btnBuscar.setOnAction(e -> DialogoBuscar.mostrar(servicio));
        btnModificar.setOnAction(e -> {
            Producto sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                DialogoModificar.mostrar(servicio, sel, this::actualizarTabla);
            } else {
                mostrarAlerta("Selección requerida",
                    "Por favor selecciona un producto de la tabla para modificarlo.",
                    Alert.AlertType.WARNING);
            }
        });
        btnEliminar.setOnAction(e -> {
            Producto sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                eliminarConConfirmacion(sel);
            } else {
                mostrarAlerta("Selección requerida",
                    "Por favor selecciona un producto de la tabla para eliminarlo.",
                    Alert.AlertType.WARNING);
            }
        });

        // Pie del sidebar — muestra el archivo de datos
        VBox pie = new VBox(4);
        pie.getStyleClass().add("sidebar-footer");
        Label lblArchivo = new Label("📄 datos/productos.txt");
        lblArchivo.getStyleClass().add("sidebar-footer-text");
        Label lblEstado = new Label("● Conectado");
        lblEstado.getStyleClass().add("sidebar-status");
        pie.getChildren().addAll(lblArchivo, lblEstado);

        Region espacioFlexible = new Region();
        VBox.setVgrow(espacioFlexible, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            logoBox, sep,
            btnListar, btnAgregar, btnBuscar, btnModificar, btnEliminar,
            espacioFlexible, pie
        );

        return sidebar;
    }

    private Button crearNavBtn(String texto, boolean activo) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("nav-btn");
        if (activo) btn.getStyleClass().add("nav-btn-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    // ═══════════════════════════════════════════════════════
    // ÁREA PRINCIPAL
    // ═══════════════════════════════════════════════════════

    private VBox crearAreaPrincipal() {
        VBox area = new VBox();
        area.getStyleClass().add("main-area");

        // Barra superior con título y botón rápido
        HBox topBar = crearTopBar();

        // Tarjetas de estadísticas
        HBox statsRow = crearStatsRow();

        // Tabla de productos
        tabla = crearTabla();
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // Barra de estado inferior
        HBox statusBar = crearStatusBar();

        area.getChildren().addAll(topBar, statsRow, tabla, statusBar);
        return area;
    }

    private HBox crearTopBar() {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("top-bar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("Catálogo de Productos");
        titulo.getStyleClass().add("top-title");

        TextField busqueda = new TextField();
        busqueda.setPromptText("🔍  Buscar por nombre...");
        busqueda.getStyleClass().add("search-field");
        HBox.setHgrow(busqueda, Priority.ALWAYS);

        // Filtro en tiempo real
        busqueda.textProperty().addListener((obs, viejo, nuevo) -> {
            filtrarTabla(nuevo);
        });

        Button btnAgregar = new Button("+ Agregar Producto");
        btnAgregar.getStyleClass().add("btn-primary");
        btnAgregar.setOnAction(e ->
            DialogoAgregar.mostrar(servicio, this::actualizarTabla)
        );

        bar.getChildren().addAll(titulo, busqueda, btnAgregar);
        return bar;
    }

    private HBox crearStatsRow() {
        HBox row = new HBox(12);
        row.getStyleClass().add("stats-row");

        lblTotalProductos = new Label("0");
        lblValorInventario = new Label("$0.00");
        lblStockBajo = new Label("0");

        VBox card1 = crearStatCard("TOTAL PRODUCTOS", lblTotalProductos, "Registros activos", "stat-info");
        VBox card2 = crearStatCard("VALOR INVENTARIO", lblValorInventario, "Valor total en stock", "stat-success");
        VBox card3 = crearStatCard("STOCK BAJO", lblStockBajo, "Menos de 10 unidades", "stat-warning");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);

        row.getChildren().addAll(card1, card2, card3);
        return row;
    }

    private VBox crearStatCard(String etiqueta, Label valorLabel, String sub, String estilo) {
        VBox card = new VBox(4);
        card.getStyleClass().addAll("stat-card", estilo);

        Label lbl = new Label(etiqueta);
        lbl.getStyleClass().add("stat-label");

        valorLabel.getStyleClass().add("stat-value");

        Label subLbl = new Label(sub);
        subLbl.getStyleClass().add("stat-sub");

        card.getChildren().addAll(lbl, valorLabel, subLbl);
        return card;
    }

    @SuppressWarnings("unchecked")
    private TableView<Producto> crearTabla() {
        TableView<Producto> tv = new TableView<>();
        tv.getStyleClass().add("tabla-productos");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No hay productos registrados.\nUsa el botón '+ Agregar Producto' para comenzar."));

        TableColumn<Producto, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));
        colId.setMaxWidth(60);
        colId.getStyleClass().add("col-id");

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre del Producto");
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));

        TableColumn<Producto, Number> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrecio()));
        colPrecio.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item.doubleValue()));
                    getStyleClass().add("cell-precio");
                }
            }
        });

        TableColumn<Producto, Number> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCantidad()));
        colCantidad.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    // Resaltamos en amarillo si el stock es bajo
                    if (item.intValue() < 10) {
                        getStyleClass().add("cell-stock-bajo");
                    } else {
                        getStyleClass().remove("cell-stock-bajo");
                    }
                }
            }
        });

        // Columna de acciones con botones editar/eliminar
        TableColumn<Producto, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<Producto, Void>() {
            private final Button btnEdit = new Button("✏ Editar");
            private final Button btnDel  = new Button("🗑 Eliminar");
            private final HBox box = new HBox(6, btnEdit, btnDel);

            {
                btnEdit.getStyleClass().add("btn-edit");
                btnDel.getStyleClass().add("btn-delete");
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    Producto p = getTableView().getItems().get(getIndex());
                    DialogoModificar.mostrar(servicio, p, () -> actualizarTabla());
                });
                btnDel.setOnAction(e -> {
                    Producto p = getTableView().getItems().get(getIndex());
                    eliminarConConfirmacion(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(colId, colNombre, colPrecio, colCantidad, colAcciones);
        listaObservable = FXCollections.observableArrayList();
        tv.setItems(listaObservable);
        return tv;
    }

    private HBox crearStatusBar() {
        HBox bar = new HBox(20);
        bar.getStyleClass().add("status-bar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Label archivo = new Label("● datos/productos.txt");
        archivo.getStyleClass().add("status-archivo");

        Region flex = new Region();
        HBox.setHgrow(flex, Priority.ALWAYS);

        Label version = new Label("Java SE  |  Arquitectura en Capas  |  Programación III");
        version.getStyleClass().add("status-version");

        bar.getChildren().addAll(archivo, flex, version);
        return bar;
    }

    // ═══════════════════════════════════════════════════════
    // LÓGICA DE UI
    // ═══════════════════════════════════════════════════════

    /**
     * Recarga la tabla desde el servicio y actualiza las estadísticas.
     * Se llama cada vez que se hace una operación CRUD.
     */
    void actualizarTabla() {
        List<Producto> productos = servicio.listarProductos();
        listaObservable.setAll(productos);
        actualizarEstadisticas(productos);
    }

    private void filtrarTabla(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            actualizarTabla();
            return;
        }
        List<Producto> todos = servicio.listarProductos();
        String filtro = texto.toLowerCase().trim();
        listaObservable.setAll(
            todos.stream()
                 .filter(p -> p.getNombre().toLowerCase().contains(filtro))
                 .collect(java.util.stream.Collectors.toList())
        );
    }

    private void actualizarEstadisticas(List<Producto> lista) {
        lblTotalProductos.setText(String.valueOf(lista.size()));

        double total = lista.stream()
            .mapToDouble(p -> p.getPrecio() * p.getCantidad())
            .sum();
        lblValorInventario.setText(String.format("$%.2f", total));

        long bajos = lista.stream().filter(p -> p.getCantidad() < 10).count();
        lblStockBajo.setText(String.valueOf(bajos));
    }

    private void eliminarConConfirmacion(Producto p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText("¿Eliminar este producto?");
        confirm.setContentText(
            "ID: " + p.getId() + "\n" +
            "Nombre: " + p.getNombre() + "\n" +
            "Esta acción no se puede deshacer."
        );

        // Aplicamos estilo al diálogo
        confirm.getDialogPane().getStylesheets().add(
            getClass().getResource("/presentacion/estilos.css").toExternalForm()
        );
        confirm.getDialogPane().getStyleClass().add("dialogo-oscuro");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                String resultado = servicio.eliminarProducto(String.valueOf(p.getId()));
                mostrarAlerta("Resultado", resultado,
                    resultado.startsWith("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
                actualizarTabla();
            }
        });
    }

    static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
