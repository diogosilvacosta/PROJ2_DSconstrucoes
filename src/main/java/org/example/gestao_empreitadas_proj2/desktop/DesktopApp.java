package org.example.gestao_empreitadas_proj2.desktop;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.gestao_empreitadas_proj2.models.*;
import org.example.gestao_empreitadas_proj2.services.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DesktopApp extends Application {

    // ── Design System: Structural Blueprint ──────────────────────
    private static final String C_PRIMARY      = "#00294f";
    private static final String C_ACCENT       = "#a53c00";
    private static final String C_BG           = "#fff9ef";
    private static final String C_SURFACE      = "#ffffff";
    private static final String C_SURFACE_LOW  = "#f9f3ea";
    private static final String C_SURFACE_HIGH = "#ede7de";
    private static final String C_TEXT         = "#1d1b16";
    private static final String C_TEXT_MUTED   = "#43474f";

    // ── Cores dinâmicas (mudam com dark mode) ───────────────────
    private String bg()         { return isDarkMode ? "#18140f" : C_BG; }
    private String surface()    { return isDarkMode ? "#26211c" : C_SURFACE; }
    private String surfaceLow() { return isDarkMode ? "#1e1a15" : C_SURFACE_LOW; }
    private String text()       { return isDarkMode ? "#ece0d0" : C_TEXT; }
    private String textMuted()  { return isDarkMode ? "#9d8f80" : C_TEXT_MUTED; }
    private String primary()    { return isDarkMode ? "#a6c9fd" : C_PRIMARY; }
    private String border()     { return isDarkMode ? "#4a4038" : "#e0d9d0"; }

    private static ConfigurableApplicationContext springCtx;

    // Services
    private ClienteService       clienteService;
    private PropostaService      propostaService;
    private ObraService          obraService;
    private AutomedicaoService   automedicaoService;
    private FaturaService        faturaService;
    private ObraMaterialService  materialService;
    private ObraMaodeobraService maodeobraService;
    private FuncionarioService   funcionarioService;
    private EsbocoService        esbocoService;

    // Observable data
    private final ObservableList<Cliente>      clientes     = FXCollections.observableArrayList();
    private final ObservableList<Proposta>     propostas    = FXCollections.observableArrayList();
    private final ObservableList<Obra>         obras        = FXCollections.observableArrayList();
    private final ObservableList<Automedicao>  automedicoes = FXCollections.observableArrayList();
    private final ObservableList<Fatura>       faturas      = FXCollections.observableArrayList();
    private final ObservableList<ObraMaterial> materiais    = FXCollections.observableArrayList();
    private final ObservableList<ObraMaodeobra>maodeobra    = FXCollections.observableArrayList();
    private final ObservableList<Funcionario>  funcionarios = FXCollections.observableArrayList();

    // State
    private Obra       obraSelecionada;
    private Label      obraInfoMateriais;
    private Label      obraInfoMaoDeObra;
    private BorderPane mainRoot;
    private BorderPane contentPane;
    private Label      labelPaginaTitulo;
    private boolean    isDarkMode = false;

    // Navigation
    private final List<Button> navBtns  = new ArrayList<>();
    private final List<Label>  navTexts = new ArrayList<>();
    private VBox   sidebar;
    private HBox   topBar;

    // Esboços na aba Obras
    private HBox  esbocosThumbnails;
    private Label labelEsbocosInfo;
    private static final String[] NAV_LABELS = {
        "Dashboard", "Clientes", "Propostas", "Obras", "Funcionarios",
        "Materiais", "Mao de Obra", "Auto-Medicoes", "Faturas", "Custos", "Esbocos"
    };

    public static void setContext(ConfigurableApplicationContext ctx) {
        springCtx = ctx;
    }

    @Override
    public void init() {
        if (springCtx == null) throw new IllegalStateException("Use DesktopLauncher.");
        clienteService     = springCtx.getBean(ClienteService.class);
        propostaService    = springCtx.getBean(PropostaService.class);
        obraService        = springCtx.getBean(ObraService.class);
        automedicaoService = springCtx.getBean(AutomedicaoService.class);
        faturaService      = springCtx.getBean(FaturaService.class);
        materialService    = springCtx.getBean(ObraMaterialService.class);
        maodeobraService   = springCtx.getBean(ObraMaodeobraService.class);
        funcionarioService = springCtx.getBean(FuncionarioService.class);
        esbocoService      = springCtx.getBean(EsbocoService.class);
    }

    @Override
    public void start(Stage stage) {
        mainRoot = new BorderPane();
        mainRoot.setLeft(criarSidebar());
        mainRoot.setCenter(criarAreaConteudo());

        carregarDados();
        navegar(0); // Clientes por defeito

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double largura = Math.min(1400, Math.max(1060, bounds.getWidth()  * 0.92));
        double altura  = Math.min(860,  Math.max(700,  bounds.getHeight() * 0.90));

        stage.setTitle("DS Construcoes | Gestao Interna");
        aplicarIcone(stage);

        Scene scene = new Scene(mainRoot, largura, altura);
        var css = DesktopApp.class.getResource("/desktop-style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        if (springCtx != null) springCtx.close();
    }

    // ═══════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ═══════════════════════════════════════════════════════════════

    private VBox criarSidebar() {
        sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setMaxWidth(220);
        sidebar.setStyle("-fx-background-color: " + C_PRIMARY + ";");

        // Brand
        VBox brand = new VBox(4);
        brand.setPadding(new Insets(24, 20, 20, 20));
        brand.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = criarLogoView(48);
        Label brandName = new Label("DS Construcoes");
        brandName.setStyle("-fx-font-size: 15; -fx-font-weight: 900; -fx-text-fill: white;");
        Label brandSub = new Label("GESTAO DE EMPREITADAS");
        brandSub.setStyle("-fx-font-size: 8.5; -fx-font-weight: 600; -fx-text-fill: rgba(255,255,255,0.42); -fx-letter-spacing: 1.2;");

        if (logo != null) {
            HBox row = new HBox(10, logo, new VBox(2, brandName, brandSub));
            row.setAlignment(Pos.CENTER_LEFT);
            brand.getChildren().add(row);
        } else {
            brand.getChildren().addAll(brandName, brandSub);
        }

        // Divider
        Region div1 = dividerH();

        // Section label
        Label secLabel = new Label("NAVEGACAO PRINCIPAL");
        secLabel.setStyle("-fx-font-size: 9; -fx-font-weight: 700; -fx-text-fill: rgba(255,255,255,0.38); -fx-padding: 18 20 8 20;");

        // Nav buttons
        VBox navArea = new VBox(3);
        navArea.setPadding(new Insets(0, 10, 0, 10));
        for (int i = 0; i < NAV_LABELS.length; i++) {
            Button btn = criarNavBtn(NAV_LABELS[i], i);
            navBtns.add(btn);
            navArea.getChildren().add(btn);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom
        Region div2 = dividerH();
        VBox footer = new VBox(4);
        footer.setPadding(new Insets(14, 20, 22, 20));
        Label ver = new Label("DS Construcoes  v2.0");
        ver.setStyle("-fx-font-size: 10; -fx-text-fill: rgba(255,255,255,0.28);");
        footer.getChildren().add(ver);

        sidebar.getChildren().addAll(brand, div1, secLabel, navArea, spacer, div2, footer);
        return sidebar;
    }

    private Button criarNavBtn(String label, int index) {
        Label txt = new Label(label);
        txt.setStyle("-fx-font-size: 13; -fx-font-weight: 600; -fx-text-fill: rgba(255,255,255,0.70);");
        navTexts.add(txt);

        HBox row = new HBox(10, txt);
        row.setAlignment(Pos.CENTER_LEFT);

        Button btn = new Button();
        btn.setGraphic(row);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 14, 10, 14));
        btn.setStyle(estiloNav(false));

        btn.setOnAction(e -> navegar(index));
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyleClass().contains("nav-active")) {
                btn.setStyle(estiloNavHover());
                txt.setStyle("-fx-font-size: 13; -fx-font-weight: 600; -fx-text-fill: white;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyleClass().contains("nav-active")) {
                btn.setStyle(estiloNav(false));
                txt.setStyle("-fx-font-size: 13; -fx-font-weight: 600; -fx-text-fill: rgba(255,255,255,0.70);");
            }
        });
        return btn;
    }

    private String estiloNav(boolean active) {
        if (active) return "-fx-background-color: rgba(165,60,0,0.28); -fx-background-radius: 10;"
                       + "-fx-text-fill: white; -fx-border-color: transparent transparent transparent #a53c00;"
                       + "-fx-border-width: 0 0 0 3; -fx-cursor: hand;";
        return "-fx-background-color: transparent; -fx-background-radius: 10;"
             + "-fx-text-fill: rgba(255,255,255,0.62); -fx-border-color: transparent; -fx-cursor: hand;";
    }

    private String estiloNavHover() {
        return "-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 10;"
             + "-fx-text-fill: rgba(255,255,255,0.88); -fx-border-color: transparent; -fx-cursor: hand;";
    }

    private void navegar(int index) {
        for (int i = 0; i < navBtns.size(); i++) {
            boolean ativo = (i == index);
            navBtns.get(i).getStyleClass().remove("nav-active");
            navBtns.get(i).setStyle(estiloNav(ativo));
            if (ativo) navBtns.get(i).getStyleClass().add("nav-active");
            // atualizar cor do label de texto
            if (i < navTexts.size()) {
                navTexts.get(i).setStyle(
                    "-fx-font-size: 13; -fx-font-weight: 600; -fx-text-fill: "
                    + (ativo ? "white;" : "rgba(255,255,255,0.70);")
                );
            }
        }
        if (labelPaginaTitulo != null && index >= 0 && index < NAV_LABELS.length)
            labelPaginaTitulo.setText(NAV_LABELS[index]);
        if (contentPane == null) return;
        switch (index) {
            case 0  -> contentPane.setCenter(criarPainelDashboard());
            case 1  -> contentPane.setCenter(criarPainelClientes());
            case 2  -> contentPane.setCenter(criarPainelPropostas());
            case 3  -> contentPane.setCenter(criarPainelObras());
            case 4  -> contentPane.setCenter(criarPainelFuncionarios());
            case 5  -> contentPane.setCenter(criarPainelMateriais());
            case 6  -> contentPane.setCenter(criarPainelMaodeobra());
            case 7  -> contentPane.setCenter(criarPainelAutomedicoes());
            case 8  -> contentPane.setCenter(criarPainelFaturas());
            case 9  -> contentPane.setCenter(criarPainelCustos());
            case 10 -> contentPane.setCenter(criarPainelEsbocos());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CONTENT AREA
    // ═══════════════════════════════════════════════════════════════

    private BorderPane criarAreaConteudo() {
        contentPane = new BorderPane();
        contentPane.setTop(criarTopBar());
        contentPane.setStyle("-fx-background-color: " + C_BG + ";");
        return contentPane;
    }

    private HBox criarTopBar() {
        topBar = new HBox(16);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + C_BG + ";"
                      + "-fx-border-color: transparent transparent " + C_SURFACE_HIGH + " transparent;"
                      + "-fx-border-width: 0 0 1 0;");

        labelPaginaTitulo = new Label("Clientes");
        labelPaginaTitulo.setStyle("-fx-font-size: 22; -fx-font-weight: 800; -fx-text-fill: " + C_PRIMARY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnTheme = btnEstilo("Modo Escuro", C_PRIMARY);
        btnTheme.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            if (isDarkMode) {
                mainRoot.getStyleClass().add("dark");
                btnTheme.setText("Modo Claro");
                btnTheme.setStyle(estiloBtn(C_ACCENT));
                // Sidebar escura mais profunda
                sidebar.setStyle("-fx-background-color: #0a1520;");
                // Topbar escura
                topBar.setStyle("-fx-background-color: #18140f; -fx-border-color: transparent transparent #312c26 transparent; -fx-border-width: 0 0 1 0;");
                // Content area escura
                contentPane.setStyle("-fx-background-color: #18140f;");
                // Titulo da pagina
                labelPaginaTitulo.setStyle("-fx-font-size: 22; -fx-font-weight: 800; -fx-text-fill: #a6c9fd;");
            } else {
                mainRoot.getStyleClass().remove("dark");
                btnTheme.setText("Modo Escuro");
                btnTheme.setStyle(estiloBtn(C_PRIMARY));
                // Restaurar cores claras
                sidebar.setStyle("-fx-background-color: " + C_PRIMARY + ";");
                topBar.setStyle("-fx-background-color: " + C_BG + "; -fx-border-color: transparent transparent " + C_SURFACE_HIGH + " transparent; -fx-border-width: 0 0 1 0;");
                contentPane.setStyle("-fx-background-color: " + C_BG + ";");
                labelPaginaTitulo.setStyle("-fx-font-size: 22; -fx-font-weight: 800; -fx-text-fill: " + C_PRIMARY + ";");
            }
            // Recarregar o painel atual com o novo tema
            navegar(navBtns.indexOf(
                navBtns.stream().filter(b -> b.getStyleClass().contains("nav-active")).findFirst().orElse(navBtns.get(0))
            ));
        });

        topBar.getChildren().addAll(labelPaginaTitulo, spacer, btnTheme);
        return topBar;
    }

    // ═══════════════════════════════════════════════════════════════
    //  PANELS
    // ═══════════════════════════════════════════════════════════════

    // ═══════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ═══════════════════════════════════════════════════════════════

    private ScrollPane criarPainelDashboard() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: " + bg() + ";");

        // ── Saudação ──────────────────────────────────────────────
        Label saudacao = new Label("Bem-vindo, DS Construções");
        saudacao.setStyle("-fx-font-size: 22; -fx-font-weight: 900; -fx-text-fill: " + primary() + ";");
        Label subSaudacao = new Label("Aqui está o resumo do estado atual do negócio.");
        subSaudacao.setStyle("-fx-font-size: 13; -fx-text-fill: " + textMuted() + ";");

        // ── KPI Cards ─────────────────────────────────────────────
        long totalClientes  = clientes.size();
        long obrasAtivas    = obras.stream().filter(o -> !"CONCLUIDA".equals(o.getEstado())).count();
        long fatPendentes   = faturas.stream().filter(f -> !"PAGA".equals(f.getEstado())).count();
        java.math.BigDecimal receitaTotal = faturas.stream()
            .filter(f -> "PAGA".equals(f.getEstado()))
            .map(Fatura::getValor)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        HBox kpiRow = new HBox(16);
        kpiRow.getChildren().addAll(
            kpiCard("👥  Clientes",           String.valueOf(totalClientes),        C_PRIMARY),
            kpiCard("🏗  Obras Ativas",        String.valueOf(obrasAtivas),          "#2563eb"),
            kpiCard("📄  Faturas Pendentes",   String.valueOf(fatPendentes),         C_ACCENT),
            kpiCard("💶  Receita Total",        String.format("%.0f€", receitaTotal), "#16a34a")
        );

        // ── Alertas ───────────────────────────────────────────────
        VBox alertasBox = new VBox(10);
        Label lblAlertas = new Label("⚠  Alertas");
        lblAlertas.setStyle("-fx-font-size: 15; -fx-font-weight: 800; -fx-text-fill: " + primary() + ";");
        alertasBox.getChildren().add(lblAlertas);

        boolean temAlertas = false;

        // Faturas a vencer em 7 dias
        java.time.LocalDate hoje = java.time.LocalDate.now();
        for (Fatura f : faturas) {
            if (!"PAGA".equals(f.getEstado()) && f.getDatavencimento() != null) {
                long dias = java.time.temporal.ChronoUnit.DAYS.between(hoje, f.getDatavencimento());
                if (dias >= 0 && dias <= 7) {
                    alertasBox.getChildren().add(alertaCard(
                        "🔔  Fatura #" + f.getNumerofatura() + " vence em " + dias + " dia(s)",
                        String.format("Valor: %.2f€  |  Vencimento: %s", f.getValor(), f.getDatavencimento()),
                        "#b45309"));
                    temAlertas = true;
                } else if (dias < 0) {
                    alertasBox.getChildren().add(alertaCard(
                        "❌  Fatura #" + f.getNumerofatura() + " está VENCIDA há " + Math.abs(dias) + " dia(s)",
                        String.format("Valor: %.2f€  |  Venceu em: %s", f.getValor(), f.getDatavencimento()),
                        "#dc2626"));
                    temAlertas = true;
                }
            }
        }

        // Propostas enviadas há mais de 14 dias sem resposta
        for (Proposta p : propostas) {
            if ("ENVIADA".equals(p.getEstado()) && p.getDataproposta() != null) {
                long dias = java.time.temporal.ChronoUnit.DAYS.between(p.getDataproposta(), hoje);
                if (dias > 14) {
                    alertasBox.getChildren().add(alertaCard(
                        "📩  Proposta #" + p.getId() + " sem resposta há " + dias + " dia(s)",
                        "Cliente: " + nomeCliente(p.getClienteid()) + "  |  Valor: " + String.format("%.2f€", p.getValortotal()),
                        "#7c3aed"));
                    temAlertas = true;
                }
            }
        }

        if (!temAlertas) {
            Label semAlertas = new Label("  ✅  Tudo em ordem! Sem alertas pendentes.");
            semAlertas.setStyle("-fx-font-size: 13; -fx-text-fill: #16a34a; -fx-padding: 12 16;"
                + "-fx-background-color: #f0fdf4; -fx-background-radius: 10;"
                + "-fx-border-color: #bbf7d0; -fx-border-radius: 10;");
            alertasBox.getChildren().add(semAlertas);
        }

        // ── Obras Recentes ────────────────────────────────────────
        VBox obrasRecentes = new VBox(10);
        Label lblObras = new Label("🏗  Obras Recentes");
        lblObras.setStyle("-fx-font-size: 15; -fx-font-weight: 800; -fx-text-fill: " + primary() + ";");
        obrasRecentes.getChildren().add(lblObras);

        obras.stream().limit(5).forEach(o -> {
            HBox row = new HBox(16);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: " + surface() + "; -fx-background-radius: 10;"
                + "-fx-border-color: " + border() + "; -fx-border-radius: 10;");

            Label obraId = new Label("Obra " + idObra(o));
            obraId.setStyle("-fx-font-size: 13; -fx-font-weight: 700; -fx-text-fill: " + primary() + "; -fx-min-width: 80;");
            Label obraCliente = new Label(nomeCliente(o.getClienteid()));
            obraCliente.setStyle("-fx-font-size: 12; -fx-text-fill: " + text() + ";");

            String estadoCor = switch (o.getEstado() == null ? "" : o.getEstado()) {
                case "CONCLUIDA"    -> "#16a34a";
                case "EM_PROGRESSO" -> "#2563eb";
                case "SUSPENSA"     -> "#dc2626";
                default             -> C_ACCENT;
            };
            Label estado = new Label(o.getEstado() == null ? "-" : o.getEstado());
            estado.setStyle("-fx-font-size: 11; -fx-font-weight: 700; -fx-text-fill: white;"
                + "-fx-background-color: " + estadoCor + "; -fx-background-radius: 6; -fx-padding: 3 8;");

            Label perc = new Label((o.getPercentagemconclusao() == null ? "0" : o.getPercentagemconclusao()) + "%");
            perc.setStyle("-fx-font-size: 12; -fx-font-weight: 600; -fx-text-fill: " + textMuted() + ";");

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            row.getChildren().addAll(obraId, obraCliente, sp, estado, perc);
            obrasRecentes.getChildren().add(row);
        });

        if (obras.isEmpty()) {
            Label sem = new Label("Sem obras registadas.");
            sem.setStyle("-fx-font-size: 12; -fx-text-fill: " + textMuted() + ";");
            obrasRecentes.getChildren().add(sem);
        }

        // ── Ações Rápidas ─────────────────────────────────────────
        Label lblAcoes = new Label("⚡  Ações Rápidas");
        lblAcoes.setStyle("-fx-font-size: 15; -fx-font-weight: 800; -fx-text-fill: " + primary() + ";");

        HBox acoes = new HBox(12);
        Button btnNovoCliente  = btnEstilo("+ Novo Cliente",   C_PRIMARY);
        Button btnNovaObra     = btnEstilo("+ Nova Obra",      C_ACCENT);
        Button btnNovaFatura   = btnEstilo("+ Nova Fatura",    "#2563eb");
        Button btnVerCustos    = btnEstilo("📊  Ver Custos",   "#16a34a");

        btnNovoCliente.setOnAction(e -> navegar(1));
        btnNovaObra.setOnAction(e -> navegar(3));
        btnNovaFatura.setOnAction(e -> navegar(8));
        btnVerCustos.setOnAction(e -> navegar(9));

        acoes.getChildren().addAll(btnNovoCliente, btnNovaObra, btnNovaFatura, btnVerCustos);

        root.getChildren().addAll(saudacao, subSaudacao, kpiRow, alertasBox, obrasRecentes, lblAcoes, acoes);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + bg() + "; -fx-background: " + bg() + ";");
        return scroll;
    }

    private VBox kpiCard(String titulo, String valor, String cor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20, 20, 20, 22));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: " + surface() + "; -fx-background-radius: 14;"
            + "-fx-border-color: transparent transparent transparent " + cor + ";"
            + "-fx-border-width: 0 0 0 5; -fx-border-radius: 14;"
            + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.09),16,0,0,3);");
        Label t = new Label(titulo);
        t.setStyle("-fx-font-size: 11; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + "; -fx-letter-spacing: 0.5;");
        Label v = new Label(valor);
        v.setStyle("-fx-font-size: 28; -fx-font-weight: 900; -fx-text-fill: " + cor + ";");
        card.getChildren().addAll(t, v);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox alertaCard(String titulo, String detalhe, String cor) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + surface() + "; -fx-background-radius: 10;"
            + "-fx-border-color: transparent transparent transparent " + cor + ";"
            + "-fx-border-width: 0 0 0 4; -fx-border-radius: 10;");
        Region barra = new Region();
        barra.setPrefWidth(4); barra.setMinWidth(4);
        VBox txt = new VBox(2);
        Label t = new Label(titulo);
        t.setStyle("-fx-font-size: 13; -fx-font-weight: 700; -fx-text-fill: " + cor + ";");
        Label d = new Label(detalhe);
        d.setStyle("-fx-font-size: 11; -fx-text-fill: " + textMuted() + ";");
        txt.getChildren().addAll(t, d);
        card.getChildren().add(txt);
        return card;
    }

    // ═══════════════════════════════════════════════════════════════
    //  FUNCIONÁRIOS
    // ═══════════════════════════════════════════════════════════════

    private BorderPane criarPainelFuncionarios() {
        TableView<Funcionario> tabela = new TableView<>(funcionarios);
        tabela.getColumns().addAll(
            coluna("ID",    "id",               60),
            coluna("Nome",  "nome",             200),
            coluna("Email", "email",            200),
            coluna("Cargo", "cargo",            150),
            coluna("Horas", "horasdisponiveis", 80)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox form = painelLateral("Registar Funcionário");
        TextField tfNome  = campo("Nome completo *");
        TextField tfEmail = campo("Email *");
        TextField tfCargo = campo("Cargo / Função *");
        TextField tfHoras = campo("Horas disponíveis");
        Label status = statusLabel();

        Button guardar = btnEstiloCor("Registar Funcionário", C_ACCENT);
        guardar.setOnAction(e -> {
            try {
                Funcionario f = new Funcionario();
                f.setNome(tfNome.getText().trim());
                f.setEmail(tfEmail.getText().trim());
                f.setCargo(tfCargo.getText().trim());
                f.setHorasdisponiveis(tfHoras.getText().isBlank() ? 0 : Integer.parseInt(tfHoras.getText().trim()));
                funcionarioService.registarFuncionario(f);
                carregarFuncionarios();
                status(status, true, "Funcionário registado com sucesso.");
                limpar(tfNome, tfEmail, tfCargo, tfHoras);
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });

        // Preencher form ao selecionar linha
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;
            tfNome.setText(sel.getNome() != null ? sel.getNome() : "");
            tfEmail.setText(sel.getEmail() != null ? sel.getEmail() : "");
            tfCargo.setText(sel.getCargo() != null ? sel.getCargo() : "");
            tfHoras.setText(sel.getHorasdisponiveis() != null ? sel.getHorasdisponiveis().toString() : "");
        });

        Button editar = btnEstiloCor("Guardar Alterações", C_PRIMARY);
        editar.setOnAction(e -> {
            Funcionario sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Seleciona um funcionário na tabela."); return; }
            try {
                sel.setNome(tfNome.getText().trim());
                sel.setEmail(tfEmail.getText().trim());
                sel.setCargo(tfCargo.getText().trim());
                sel.setHorasdisponiveis(tfHoras.getText().isBlank() ? 0 : Integer.parseInt(tfHoras.getText().trim()));
                funcionarioService.atualizarFuncionario(sel);
                carregarFuncionarios();
                status(status, true, "Funcionário atualizado.");
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });

        Button eliminar = btnEstiloCor("Eliminar Selecionado", "#dc2626");
        eliminar.setOnAction(e -> {
            Funcionario sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Seleciona um funcionário na tabela."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar");
            confirm.setHeaderText(null);
            confirm.setContentText("Eliminar funcionário \"" + sel.getNome() + "\"?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    funcionarioService.eliminarFuncionario(sel.getId());
                    carregarFuncionarios();
                    limpar(tfNome, tfEmail, tfCargo, tfHoras);
                    status(status, true, "Funcionário eliminado.");
                }
            });
        });

        form.getChildren().addAll(
            label("Nome"),  tfNome,
            label("Email"), tfEmail,
            label("Cargo"), tfCargo,
            label("Horas Disponíveis"), tfHoras,
            guardar, editar, eliminar, status
        );

        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarFuncionarios));
    }

    private BorderPane criarPainelClientes() {
        TableView<Cliente> tabela = new TableView<>(clientes);
        tabela.getColumns().addAll(
            coluna("ID", "id", 60),
            coluna("Nome", "nome", 220),
            coluna("Email", "email", 220),
            coluna("NIF", "nif", 100),
            coluna("Telefone", "telefone", 120)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox form = painelLateral("Registar Novo Cliente");
        TextField nome = campo("Nome completo *"), email = campo("Email *"),
                  nif  = campo("NIF *"),            telefone = campo("Telefone"),
                  morada = campo("Morada");
        Label status = statusLabel();
        Button guardar = btnEstiloCor("Registar Cliente", C_ACCENT);
        guardar.setOnAction(e -> {
            try {
                Cliente c = new Cliente();
                c.setNome(nome.getText().trim());
                c.setEmail(email.getText().trim());
                c.setNif(nif.getText().trim());
                c.setTelefone(telefone.getText().trim());
                c.setMorada(morada.getText().trim());
                clienteService.registarCliente(c);
                carregarClientes();
                status(status, true, "Cliente registado com sucesso.");
                limpar(nome, email, nif, telefone, morada);
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });
        // Preencher form ao selecionar na tabela
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;
            nome.setText(sel.getNome() != null ? sel.getNome() : "");
            email.setText(sel.getEmail() != null ? sel.getEmail() : "");
            nif.setText(sel.getNif() != null ? sel.getNif() : "");
            telefone.setText(sel.getTelefone() != null ? sel.getTelefone() : "");
            morada.setText(sel.getMorada() != null ? sel.getMorada() : "");
        });

        Button editar = btnEstiloCor("Guardar Alterações", C_PRIMARY);
        editar.setOnAction(e -> {
            Cliente sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Seleciona um cliente na tabela."); return; }
            sel.setNome(nome.getText().trim());
            sel.setEmail(email.getText().trim());
            sel.setNif(nif.getText().trim());
            sel.setTelefone(telefone.getText().trim());
            sel.setMorada(morada.getText().trim());
            clienteService.registarCliente(sel);
            carregarClientes();
            status(status, true, "Cliente atualizado.");
        });

        Button eliminar = btnEstiloCor("Eliminar Selecionado", "#dc2626");
        eliminar.setOnAction(e -> {
            Cliente sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Seleciona um cliente."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar");
            confirm.setHeaderText(null);
            confirm.setContentText("Eliminar cliente \"" + sel.getNome() + "\"?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    clienteService.eliminarCliente(sel.getId());
                    carregarClientes();
                    limpar(nome, email, nif, telefone, morada);
                    status(status, true, "Cliente eliminado.");
                }
            });
        });

        form.getChildren().addAll(
            label("Nome Completo"), nome,
            label("Email"), email,
            label("NIF"), nif,
            label("Telefone"), telefone,
            label("Morada"), morada,
            guardar, editar, eliminar, status
        );
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarClientes));
    }

    private BorderPane criarPainelPropostas() {
        // Lista filtrada
        javafx.collections.ObservableList<Proposta> propostasFiltradas = javafx.collections.FXCollections.observableArrayList(propostas);
        TableView<Proposta> tabela = new TableView<>(propostasFiltradas);
        TableColumn<Proposta, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> new SimpleStringProperty(nomeCliente(c.getValue().getClienteid())));
        TableColumn<Proposta, BigDecimal> valor = coluna("Valor", "valortotal", 120);
        configurarMoeda(valor);
        tabela.getColumns().addAll(
            coluna("ID", "id", 60), cliente,
            coluna("Estado", "estado", 110), coluna("Data", "dataproposta", 120), valor
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Filtro por estado
        HBox filtroBox = new HBox(8);
        filtroBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        filtroBox.setPadding(new Insets(0, 0, 8, 0));
        Label lblFiltro = new Label("Filtrar:");
        lblFiltro.setStyle("-fx-text-fill: " + textMuted() + "; -fx-font-size: 12;");
        ComboBox<String> cbFiltro = new ComboBox<>();
        cbFiltro.getItems().addAll("TODOS","RASCUNHO","ENVIADA","ACEITE","REJEITADA");
        cbFiltro.setValue("TODOS");
        cbFiltro.setStyle("-fx-background-color: " + surface() + "; -fx-text-fill: " + text() + "; -fx-font-size: 12;");
        cbFiltro.setOnAction(e -> {
            String f = cbFiltro.getValue();
            propostasFiltradas.setAll(
                "TODOS".equals(f) ? propostas :
                propostas.filtered(p -> f.equals(p.getEstado()))
            );
        });
        propostas.addListener((javafx.collections.ListChangeListener<Proposta>) c -> {
            String f = cbFiltro.getValue();
            propostasFiltradas.setAll(
                "TODOS".equals(f) ? propostas :
                propostas.filtered(p -> f.equals(p.getEstado()))
            );
        });
        filtroBox.getChildren().addAll(lblFiltro, cbFiltro);

        VBox form = painelLateral("Nova Proposta");
        ComboBox<Cliente>  cbCliente = comboClientes();
        ComboBox<String>   cbEstado  = comboTexto("RASCUNHO","ENVIADA","ACEITE","REJEITADA");
        TextField          tfValor   = campo("Valor total *");
        TextArea           taDesc    = area("Descricao");
        Label status = statusLabel();
        Button guardar = btnEstiloCor("Criar Proposta", C_ACCENT);
        guardar.setOnAction(e -> {
            try {
                Proposta p = new Proposta();
                p.setClienteid(cbCliente.getValue());
                p.setEstado(cbEstado.getValue());
                p.setDataproposta(LocalDate.now());
                p.setValortotal(new BigDecimal(tfValor.getText().trim()));
                p.setDescricao(taDesc.getText().trim());
                propostaService.criarProposta(p);
                carregarPropostas();
                status(status, true, "Proposta criada.");
                tfValor.clear(); taDesc.clear();
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });

        Button eliminar = btnEstiloCor("Eliminar Selecionada", "#dc2626");
        eliminar.setOnAction(e -> {
            Proposta sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Seleciona uma proposta na tabela."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminação");
            confirm.setHeaderText(null);
            confirm.setContentText("Eliminar proposta #" + sel.getId() + " (" + nomeCliente(sel.getClienteid()) + ")?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    propostaService.eliminarProposta(sel.getId());
                    carregarPropostas();
                    status(status, true, "Proposta eliminada.");
                }
            });
        });

        Separator sepEditar = new Separator();
        Label lblEditarSec = new Label("ALTERAR ESTADO DA SELECIONADA");
        lblEditarSec.setStyle("-fx-font-size: 9; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + "; -fx-letter-spacing: 1;");
        ComboBox<String> cbNovoEstado = comboTexto("RASCUNHO","ENVIADA","ACEITE","REJEITADA");
        Button alterarEstado = btnEstiloCor("Atualizar Estado", C_PRIMARY);
        alterarEstado.setOnAction(e -> {
            Proposta sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Seleciona uma proposta na tabela."); return; }
            sel.setEstado(cbNovoEstado.getValue());
            propostaService.atualizarProposta(sel);
            carregarPropostas();
            status(status, true, "Estado atualizado: " + cbNovoEstado.getValue());
        });

        form.getChildren().addAll(
            label("Cliente"), cbCliente,
            label("Estado"), cbEstado,
            label("Valor Total"), tfValor,
            label("Descricao"), taDesc,
            guardar, eliminar,
            sepEditar, lblEditarSec,
            label("Novo Estado"), cbNovoEstado,
            alterarEstado, status
        );
                BorderPane painel = painelPrincipal(tabela, form, botaoAtualizar(this::carregarPropostas));
        VBox topo = new VBox(4, new HBox(10, botaoAtualizar(this::carregarPropostas), filtroBox));
        topo.setPadding(new Insets(0, 0, 4, 0));
        painel.setTop(topo);
        return painel;
    }

    private BorderPane criarPainelObras() {
        TableView<Obra> tabela = new TableView<>(obras);
        TableColumn<Obra, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> new SimpleStringProperty(nomeCliente(c.getValue().getClienteid())));
        TableColumn<Obra, String> proposta = new TableColumn<>("Proposta");
        proposta.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPropostaid() == null ? "-" : "#" + c.getValue().getPropostaid().getId()
        ));
        tabela.getColumns().addAll(
            coluna("ID","id",60), cliente, proposta,
            coluna("Estado","estado",120), coluna("Data","datainicio",120), coluna("%","percentagemconclusao",70)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ── Painel lateral com form + botão esboços ──────────────
        VBox form = painelLateral("Nova Obra");
        ComboBox<Cliente>  cbCliente  = comboClientes();
        ComboBox<Proposta> cbProposta = comboPropostas();
        ComboBox<String>   cbEstado   = comboTexto("INICIADA","EM_PROGRESSO","SUSPENSA","CONCLUIDA");
        TextField          tfPerc     = campo("Percentagem (0-100)");
        Label status = statusLabel();
        Button guardar = btnEstiloCor("Criar Obra", C_ACCENT);
        guardar.setOnAction(e -> {
            try {
                Obra obra = new Obra();
                obra.setClienteid(cbCliente.getValue());
                obra.setPropostaid(cbProposta.getValue());
                obra.setEstado(cbEstado.getValue());
                obra.setDatainicio(LocalDate.now());
                obra.setPercentagemconclusao(parse(tfPerc.getText()));
                obraService.criarObra(obra);
                carregarObras();
                status(status, true, "Obra criada.");
                tfPerc.clear();
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });

        // ── Separador visual + botão de esboços ─────────────────
        Separator sepEsbocos = new Separator();
        Label lblEsbocosSecao = new Label("ESBOÇOS");
        lblEsbocosSecao.setStyle("-fx-font-size: 10; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + "; -fx-letter-spacing: 1.5;");

        labelEsbocosInfo = new Label("Seleciona uma obra para ver os esboços");
        labelEsbocosInfo.setWrapText(true);
        labelEsbocosInfo.setStyle("-fx-font-size: 11; -fx-text-fill: " + textMuted() + ";");

        Button btnVerEsbocos = btnEstiloCor("Ver Esboços", C_PRIMARY);
        btnVerEsbocos.setDisable(true);

        Button btnNovoEsboco = btnEstiloCor("+ Desenhar Esboço", C_ACCENT);
        btnNovoEsboco.setDisable(true);
        btnNovoEsboco.setOnAction(e -> navegar(10));

        // Editar obra selecionada
        Separator sepEditar = new Separator();
        Label lblEditarObra = new Label("EDITAR OBRA SELECIONADA");
        lblEditarObra.setStyle("-fx-font-size: 10; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + "; -fx-letter-spacing: 1.5;");
        ComboBox<String> cbEditarEstado = comboTexto("INICIADA","EM_PROGRESSO","SUSPENSA","CONCLUIDA");
        TextField tfEditarPerc = campo("Nova percentagem (0-100)");
        Label statusEditar = statusLabel();

        Button btnEditarObra = btnEstiloCor("Atualizar Obra", C_PRIMARY);
        btnEditarObra.setOnAction(e -> {
            Obra sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(statusEditar, false, "Seleciona uma obra na tabela."); return; }
            try {
                sel.setEstado(cbEditarEstado.getValue());
                if (!tfEditarPerc.getText().isBlank())
                    sel.setPercentagemconclusao(parse(tfEditarPerc.getText()));
                obraService.atualizarObra(sel);
                carregarObras();
                tfEditarPerc.clear();
                status(statusEditar, true, "Obra #" + sel.getId() + " atualizada.");
            } catch (Exception ex) { status(statusEditar, false, ex.getMessage()); }
        });

        // Preencher combo ao selecionar obra
        tabela.getSelectionModel().selectedItemProperty().addListener((obs2, o2, n2) -> {
            if (n2 != null && n2.getEstado() != null) cbEditarEstado.setValue(n2.getEstado());
        });

        form.getChildren().addAll(
            label("Cliente"), cbCliente,
            label("Proposta"), cbProposta,
            label("Estado"), cbEstado,
            label("Percentagem"), tfPerc,
            guardar, status,
            sepEditar, lblEditarObra,
            label("Estado"), cbEditarEstado,
            label("Percentagem"), tfEditarPerc,
            btnEditarObra, statusEditar,
            sepEsbocos, lblEsbocosSecao, labelEsbocosInfo,
            btnVerEsbocos, btnNovoEsboco
        );

        // ── Seleção de obra ──────────────────────────────────────
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            obraSelecionada = n;
            atualizarObraSelecionada();
            carregarMateriais();
            carregarMaodeobra();

            if (n == null) {
                btnVerEsbocos.setText("Ver Esboços");
                btnVerEsbocos.setDisable(true);
                btnNovoEsboco.setDisable(true);
                labelEsbocosInfo.setText("Seleciona uma obra para ver os esboços");
            } else {
                List<Esboco> lista = esbocoService.listarPorObra(n.getId());
                int total = lista.size();
                btnVerEsbocos.setText("Ver Esboços (" + total + ")");
                btnVerEsbocos.setDisable(false);
                btnNovoEsboco.setDisable(false);
                labelEsbocosInfo.setText(total == 0
                    ? "Nenhum esboço ainda para esta obra."
                    : total + " esboço(s) guardado(s). Clica para ver.");
                btnVerEsbocos.setOnAction(ev -> abrirGaleriaEsbocos(n, null));
            }
        });

                // Scroll no painel lateral para não cortar conteúdo
        ScrollPane formScroll = new ScrollPane(form);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        formScroll.setPrefWidth(360);
        formScroll.setMaxWidth(360);
        formScroll.setStyle("-fx-background-color: " + surface() + "; -fx-background: " + surface() + "; -fx-border-color: " + border() + "; -fx-border-width: 0 0 0 1;");

        BorderPane painel = new BorderPane();
        painel.setPadding(new Insets(16));
        painel.setStyle("-fx-background-color: " + bg() + ";");
        painel.setTop(new HBox(10, botaoAtualizar(this::carregarObras)));
        painel.setCenter(tabela);
        painel.setRight(formScroll);
        return painel;
    }

    private void atualizarEsbocos(Obra obra) {
        if (esbocosThumbnails == null) return;
        esbocosThumbnails.getChildren().clear();

        if (obra == null) {
            labelEsbocosInfo.setText("— selecione uma obra na tabela acima");
            Label msg = new Label("Nenhuma obra selecionada.");
            msg.setStyle("-fx-font-size: 12; -fx-text-fill: " + textMuted() + ";");
            esbocosThumbnails.getChildren().add(msg);
            return;
        }

        List<Esboco> lista = esbocoService.listarPorObra(obra.getId());

        if (lista.isEmpty()) {
            labelEsbocosInfo.setText("— nenhum esboço guardado para Obra #" + obra.getId());
            Label msg = new Label("Ainda não há esboços para esta obra. Vai ao separador Esboços para criar um!");
            msg.setStyle("-fx-font-size: 12; -fx-text-fill: " + textMuted() + ";");
            esbocosThumbnails.getChildren().add(msg);
            return;
        }

        labelEsbocosInfo.setText("— " + lista.size() + " esboço(s) para Obra #" + obra.getId());

        for (Esboco esboco : lista) {
            try {
                Image img = new Image(new ByteArrayInputStream(esboco.getDados()), 160, 110, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(160);
                iv.setFitHeight(110);
                iv.setPreserveRatio(true);

                String dataStr = esboco.getDataCriacao() != null
                    ? esboco.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "";
                Label dataLabel = new Label(dataStr);
                dataLabel.setStyle("-fx-font-size: 10; -fx-text-fill: " + textMuted() + ";");

                // Botão eliminar
                Button btnElim = new Button("✕");
                btnElim.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626;"
                               + "-fx-font-size: 11; -fx-font-weight: 700; -fx-cursor: hand; -fx-padding: 0 4;");
                btnElim.setOnAction(ev -> {
                    esbocoService.eliminar(esboco.getId());
                    atualizarEsbocos(obra);
                });

                HBox footer = new HBox(4, dataLabel, new Region(), btnElim);
                ((Region) footer.getChildren().get(1)).setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
                footer.setAlignment(Pos.CENTER_LEFT);

                VBox card = new VBox(6, iv, footer);
                card.setAlignment(Pos.CENTER);
                card.setPadding(new Insets(8));
                card.setStyle("-fx-background-color: " + bg() + "; -fx-background-radius: 10;"
                            + "-fx-border-color: " + border() + "; -fx-border-radius: 10; -fx-cursor: hand;");

                card.setOnMouseEntered(e -> card.setStyle(
                    "-fx-background-color: " + surfaceLow() + "; -fx-background-radius: 10;"
                  + "-fx-border-color: " + C_PRIMARY + "; -fx-border-radius: 10; -fx-cursor: hand;"));
                card.setOnMouseExited(e -> card.setStyle(
                    "-fx-background-color: " + bg() + "; -fx-background-radius: 10;"
                  + "-fx-border-color: " + border() + "; -fx-border-radius: 10; -fx-cursor: hand;"));

                card.setOnMouseClicked(e -> {
                    if (e.getTarget() != btnElim) abrirEsboco(esboco);
                });

                esbocosThumbnails.getChildren().add(card);
            } catch (Exception ex) { /* ignora esboço com erro */ }
        }
    }

    private void abrirGaleriaEsbocos(Obra obra, List<Esboco> listaIgnorada) {
        // Recarregar sempre da BD para garantir dados atualizados
        List<Esboco> lista = esbocoService.listarPorObra(obra.getId());
        Stage janela = new Stage();
        janela.setTitle("Esboços — Obra " + idObra(obra) + "  |  " + nomeCliente(obra.getClienteid()));

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + bg() + ";");

        // ── Cabeçalho ────────────────────────────────────────────
        HBox header = new HBox(12);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + C_PRIMARY + ";");
        Label titulo = new Label("Esboços — Obra " + idObra(obra));
        titulo.setStyle("-fx-font-size: 18; -fx-font-weight: 800; -fx-text-fill: white;");
        Label sub = new Label(lista.size() + " esboço(s) guardado(s)");
        sub.setStyle("-fx-font-size: 12; -fx-text-fill: rgba(255,255,255,0.65); -fx-padding: 4 0 0 0;");
        VBox hdrTxt = new VBox(2, titulo, sub);
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);
        Button btnFechar = new Button("✕  Fechar");
        btnFechar.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white;"
                         + "-fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        btnFechar.setOnAction(e -> janela.close());
        header.getChildren().addAll(hdrTxt, hSpacer, btnFechar);

        // ── Grid de miniaturas ───────────────────────────────────
        javafx.scene.layout.FlowPane grid = new javafx.scene.layout.FlowPane(16, 16);
        grid.setPadding(new Insets(24));
        grid.setPrefWrapLength(Double.MAX_VALUE); // wrapping controlado pelo ScrollPane
        grid.setStyle("-fx-background-color: " + bg() + ";");

        // Referência mutável para poder refrescar a galeria após eliminar
        final List<Esboco>[] listaRef = new List[]{new java.util.ArrayList<>(lista)};

        // Array para permitir auto-referência dentro do lambda
        final Runnable[] refrescarRef = new Runnable[1];
        refrescarRef[0] = () -> {
            final Runnable refrescar = refrescarRef[0];
            grid.getChildren().clear();
            for (Esboco esboco : listaRef[0]) {
                try {
                    Image img = new Image(new ByteArrayInputStream(esboco.getDados()), 220, 160, true, true);
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(220);
                    iv.setFitHeight(160);
                    iv.setPreserveRatio(true);

                    String dataStr = esboco.getDataCriacao() != null
                        ? esboco.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                        : "";

                    Label nome = new Label(esboco.getNome() == null ? "Esboço" : esboco.getNome());
                    nome.setMaxWidth(220);
                    nome.setStyle("-fx-font-size: 11; -fx-font-weight: 600; -fx-text-fill: " + text() + "; -fx-wrap-text: true;");
                    Label data = new Label(dataStr);
                    data.setStyle("-fx-font-size: 10; -fx-text-fill: " + textMuted() + ";");

                    Button btnElim = new Button("🗑 Eliminar");
                    btnElim.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626;"
                                   + "-fx-font-size: 11; -fx-font-weight: 700; -fx-cursor: hand; -fx-padding: 2 0;");

                    Button btnAbrir = new Button("🔍 Ver");
                    btnAbrir.setStyle("-fx-background-color: " + C_PRIMARY + "; -fx-text-fill: white;"
                                    + "-fx-font-size: 11; -fx-font-weight: 700; -fx-cursor: hand;"
                                    + "-fx-padding: 4 10; -fx-background-radius: 6;");

                    HBox acoes = new HBox(8, btnAbrir, btnElim);
                    acoes.setAlignment(Pos.CENTER_LEFT);
                    acoes.setPadding(new Insets(4, 0, 0, 0));

                    VBox card = new VBox(8, iv, nome, data, acoes);
                    card.setPadding(new Insets(12));
                    card.setStyle("-fx-background-color: " + surface() + "; -fx-background-radius: 12;"
                                + "-fx-border-color: " + border() + "; -fx-border-radius: 12;"
                                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.08),14,0,0,2);");

                    // Hover
                    card.setOnMouseEntered(e -> card.setStyle(
                        "-fx-background-color: " + surfaceLow() + "; -fx-background-radius: 12;"
                      + "-fx-border-color: " + C_PRIMARY + "; -fx-border-radius: 12;"
                      + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.14),18,0,0,4);"));
                    card.setOnMouseExited(e -> card.setStyle(
                        "-fx-background-color: " + surface() + "; -fx-background-radius: 12;"
                      + "-fx-border-color: " + border() + "; -fx-border-radius: 12;"
                      + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.08),14,0,0,2);"));

                    Esboco esbocoRef = esboco;
                    btnAbrir.setOnAction(ev -> abrirEsboco(esbocoRef));
                    card.setOnMouseClicked(ev -> {
                        if (ev.getTarget() != btnElim && ev.getTarget() != btnAbrir)
                            abrirEsboco(esbocoRef);
                    });
                    btnElim.setOnAction(ev -> {
                        esbocoService.eliminar(esbocoRef.getId());
                        listaRef[0].remove(esbocoRef);
                        sub.setText(listaRef[0].size() + " esboço(s) guardado(s)");
                        refrescar.run();
                    });

                    grid.getChildren().add(card);
                } catch (Exception ex) { /* ignora esboço com erro */ }
            }
            if (listaRef[0].isEmpty()) {
                Label vazio = new Label("Nenhum esboço disponível.");
                vazio.setStyle("-fx-font-size: 13; -fx-text-fill: " + textMuted() + ";");
                grid.getChildren().add(vazio);
            }
        };
        refrescarRef[0].run();

        // ScrollPane configurado para scroll vertical com wrap horizontal automático
        grid.setPrefWrapLength(Double.MAX_VALUE); // deixar o FlowPane usar toda a largura disponível
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);       // FlowPane ocupa toda a largura
        scroll.setFitToHeight(false);     // permite scroll vertical
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: " + bg() + "; -fx-background: " + bg() + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        // Janela responsiva — começa grande e é redimensionável
        double janelaLargura = Math.min(1300, javafx.stage.Screen.getPrimary().getVisualBounds().getWidth() * 0.90);
        double janelaAltura  = Math.min(860,  javafx.stage.Screen.getPrimary().getVisualBounds().getHeight() * 0.88);
        Scene scene = new Scene(root, janelaLargura, janelaAltura);
        var css = DesktopApp.class.getResource("/desktop-style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        janela.setScene(scene);
        janela.setResizable(true);
        janela.show();
    }

    private void abrirEsboco(Esboco esboco) {
        try {
            Image img = new Image(new ByteArrayInputStream(esboco.getDados()));
            ImageView iv = new ImageView(img);
            iv.setPreserveRatio(true);
            iv.setFitWidth(Math.min(img.getWidth(), 1200));
            iv.setFitHeight(Math.min(img.getHeight(), 800));

            ScrollPane scroll = new ScrollPane(iv);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);
            scroll.setStyle("-fx-background-color: #f0ece4; -fx-background: #f0ece4; -fx-padding: 16;");

            Stage janela = new Stage();
            janela.setTitle(esboco.getNome());

            // ── Painel lateral com botões ─────────────────────────
            String nomeTxt = esboco.getNome() == null ? "Esboço" : esboco.getNome();
            String dataStr = esboco.getDataCriacao() != null
                ? esboco.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";

            Button btnImprimir = new Button("🖨  Imprimir");
            btnImprimir.setMaxWidth(Double.MAX_VALUE);
            btnImprimir.setPrefHeight(52);
            btnImprimir.setStyle(
                "-fx-background-color: " + C_PRIMARY + "; -fx-text-fill: white;"
              + "-fx-font-weight: 800; -fx-font-size: 15; -fx-background-radius: 10; -fx-cursor: hand;");
            btnImprimir.setOnMouseEntered(e -> btnImprimir.setStyle(
                "-fx-background-color: #003d75; -fx-text-fill: white;"
              + "-fx-font-weight: 800; -fx-font-size: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
            btnImprimir.setOnMouseExited(e -> btnImprimir.setStyle(
                "-fx-background-color: " + C_PRIMARY + "; -fx-text-fill: white;"
              + "-fx-font-weight: 800; -fx-font-size: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
            btnImprimir.setOnAction(e -> {
                javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
                if (job != null) {
                    boolean ok = job.showPrintDialog(janela);
                    if (ok) {
                        javafx.print.PageLayout layout = job.getJobSettings().getPageLayout();
                        ImageView ivPrint = new ImageView(img);
                        ivPrint.setPreserveRatio(true);
                        ivPrint.setFitWidth(layout.getPrintableWidth());
                        ivPrint.setFitHeight(layout.getPrintableHeight());
                        if (job.printPage(ivPrint)) {
                            job.endJob();
                            Alert ok2 = new Alert(Alert.AlertType.INFORMATION);
                            ok2.setHeaderText(null);
                            ok2.setContentText("Esboço enviado para impressão!");
                            ok2.showAndWait();
                        }
                    }
                }
            });

            Button btnFechar = new Button("✕  Fechar");
            btnFechar.setMaxWidth(Double.MAX_VALUE);
            btnFechar.setPrefHeight(52);
            btnFechar.setStyle(
                "-fx-background-color: #a53c00; -fx-text-fill: white;"
              + "-fx-font-weight: 800; -fx-font-size: 15; -fx-background-radius: 10; -fx-cursor: hand;");
            btnFechar.setOnMouseEntered(e -> btnFechar.setStyle(
                "-fx-background-color: #c04800; -fx-text-fill: white;"
              + "-fx-font-weight: 800; -fx-font-size: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
            btnFechar.setOnMouseExited(e -> btnFechar.setStyle(
                "-fx-background-color: #a53c00; -fx-text-fill: white;"
              + "-fx-font-weight: 800; -fx-font-size: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
            btnFechar.setOnAction(e -> janela.close());

            Label lblNome = new Label(nomeTxt);
            lblNome.setWrapText(true);
            lblNome.setStyle("-fx-font-size: 17; -fx-font-weight: 900; -fx-text-fill: " + C_PRIMARY + ";");
            Label lblData = new Label(dataStr);
            lblData.setStyle("-fx-font-size: 14; -fx-font-weight: 600; -fx-text-fill: " + C_TEXT_MUTED + ";");

            Region spacer = new Region();
            spacer.setPrefHeight(24);

            VBox sidebar = new VBox(12, lblNome, lblData, spacer, btnImprimir, btnFechar);
            sidebar.setPadding(new Insets(24, 16, 24, 16));
            sidebar.setPrefWidth(180);
            sidebar.setStyle(
                "-fx-background-color: #f5f0e8;"
              + "-fx-border-color: transparent transparent transparent #e0d9d0;"
              + "-fx-border-width: 0 0 0 0;"
            );

            BorderPane root = new BorderPane();
            root.setCenter(scroll);
            root.setRight(sidebar);

            double winW = Math.min(img.getWidth() + 40, 1280);
            double winH = Math.min(img.getHeight() + 100, 900);
            Scene scene = new Scene(root, Math.max(winW, 700), Math.max(winH, 500));
            var css = DesktopApp.class.getResource("/desktop-style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            janela.setScene(scene);
            janela.setResizable(true);
            janela.show();
        } catch (Exception ex) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setHeaderText(null);
            err.setContentText("Erro ao abrir esboço: " + ex.getMessage());
            err.showAndWait();
        }
    }

    private BorderPane criarPainelMateriais() {
        TableView<ObraMaterial> tabela = new TableView<>(materiais);
        TableColumn<ObraMaterial, BigDecimal> preco = coluna("Preco", "precoUnitario", 100);
        TableColumn<ObraMaterial, BigDecimal> total = new TableColumn<>("Total");
        total.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCustoTotal()));
        configurarMoeda(preco); configurarMoeda(total);
        tabela.getColumns().addAll(
            coluna("Descricao","descricao",240), coluna("Unidade","unidade",90),
            coluna("Quantidade","quantidade",100), preco, total
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        obraInfoMateriais = subtitulo("Selecione uma obra no separador Obras primeiro.");
        VBox form = painelLateral("Adicionar Material");
        TextField desc = campo("Descricao *"), un = campo("Unidade"),
                  qtd  = campo("Quantidade *"), precoTf = campo("Preco Unitario *");
        Label status = statusLabel();
        Button guardar = btnEstiloCor("Adicionar Material", C_ACCENT);
        guardar.setOnAction(e -> {
            if (obraSelecionada == null) { status(status, false, "Selecione uma obra."); return; }
            try {
                ObraMaterial m = new ObraMaterial();
                m.setObraid(obraSelecionada);
                m.setDescricao(desc.getText().trim());
                m.setUnidade(un.getText().isBlank() ? "un" : un.getText().trim());
                m.setQuantidade(new BigDecimal(qtd.getText().trim()));
                m.setPrecoUnitario(new BigDecimal(precoTf.getText().trim()));
                materialService.adicionarMaterial(m);
                carregarMateriais();
                status(status, true, "Material adicionado.");
                limpar(desc, un, qtd, precoTf);
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });
        form.getChildren().addAll(
            label("Descricao"), desc, label("Unidade"), un,
            label("Quantidade"), qtd, label("Preco"), precoTf,
            guardar, status
        );
        BorderPane painel = painelPrincipal(tabela, form, botaoAtualizar(this::carregarMateriais));

        // ComboBox de obra direto neste painel
        ComboBox<Obra> cbObraMatLocal = comboObras();
        cbObraMatLocal.setMaxWidth(360);
        cbObraMatLocal.setPromptText("Selecionar obra...");
        if (obraSelecionada != null) cbObraMatLocal.getSelectionModel().select(obraSelecionada);
        cbObraMatLocal.setOnAction(e -> {
            obraSelecionada = cbObraMatLocal.getValue();
            atualizarObraSelecionada();
            carregarMateriais();
        });

        HBox obraRow = new HBox(12, label("Obra:"), cbObraMatLocal);
        obraRow.setAlignment(Pos.CENTER_LEFT);
        VBox topo = new VBox(8, obraRow, obraInfoMateriais);
        topo.setPadding(new Insets(14, 20, 8, 20));
        painel.setTop(topo);
        return painel;
    }

    private BorderPane criarPainelMaodeobra() {
        TableView<ObraMaodeobra> tabela = new TableView<>(maodeobra);
        TableColumn<ObraMaodeobra, String> func = new TableColumn<>("Funcionario");
        func.setCellValueFactory(c -> new SimpleStringProperty(nomeFuncionario(c.getValue().getFuncionarioid())));
        TableColumn<ObraMaodeobra, BigDecimal> custo = coluna("EUR/h","custoHora",90);
        TableColumn<ObraMaodeobra, BigDecimal> total = new TableColumn<>("Total");
        total.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCustoTotal()));
        configurarMoeda(custo); configurarMoeda(total);
        tabela.getColumns().addAll(func, coluna("Horas","horas",80), custo, total);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        obraInfoMaoDeObra = subtitulo("Selecione uma obra no separador Obras primeiro.");
        VBox form = painelLateral("Alocar Funcionario");
        ComboBox<Funcionario> cbFunc = comboFuncionarios();
        TextField horas = campo("Horas *"), custoTf = campo("Custo/hora *");
        Label status = statusLabel();
        Button guardar = btnEstiloCor("Alocar Funcionario", C_PRIMARY);
        guardar.setOnAction(e -> {
            if (obraSelecionada == null || cbFunc.getValue() == null) {
                status(status, false, "Selecione obra e funcionario."); return;
            }
            try {
                ObraMaodeobra m = new ObraMaodeobra();
                m.setObraid(obraSelecionada);
                m.setFuncionarioid(cbFunc.getValue());
                m.setHoras(Integer.parseInt(horas.getText().trim()));
                m.setCustoHora(new BigDecimal(custoTf.getText().trim()));
                maodeobraService.alocarMaodeobra(m);
                carregarMaodeobra();
                status(status, true, "Funcionario alocado.");
                limpar(horas, custoTf);
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });
        form.getChildren().addAll(
            label("Funcionario"), cbFunc,
            label("Horas"), horas,
            label("Custo/hora"), custoTf,
            guardar, status
        );
        BorderPane painel = painelPrincipal(tabela, form, botaoAtualizar(this::carregarMaodeobra));

        // ComboBox de obra direto neste painel
        ComboBox<Obra> cbObraMdoLocal = comboObras();
        cbObraMdoLocal.setMaxWidth(360);
        cbObraMdoLocal.setPromptText("Selecionar obra...");
        if (obraSelecionada != null) cbObraMdoLocal.getSelectionModel().select(obraSelecionada);
        cbObraMdoLocal.setOnAction(e -> {
            obraSelecionada = cbObraMdoLocal.getValue();
            atualizarObraSelecionada();
            carregarMaodeobra();
        });

        HBox obraRow = new HBox(12, label("Obra:"), cbObraMdoLocal);
        obraRow.setAlignment(Pos.CENTER_LEFT);
        VBox topo = new VBox(8, obraRow, obraInfoMaoDeObra);
        topo.setPadding(new Insets(14, 20, 8, 20));
        painel.setTop(topo);
        return painel;
    }

    private BorderPane criarPainelAutomedicoes() {
        TableView<Automedicao> tabela = new TableView<>(automedicoes);
        TableColumn<Automedicao, String> obra = new TableColumn<>("Obra");
        obra.setCellValueFactory(c -> new SimpleStringProperty(idObra(c.getValue().getObraid())));
        TableColumn<Automedicao, BigDecimal> valor = coluna("Valor","valor",120);
        configurarMoeda(valor);
        tabela.getColumns().addAll(
            coluna("ID","id",60), obra,
            coluna("% Trabalho","percentagemtrabalho",120),
            coluna("Estado","estado",100), valor
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox form = painelLateral("Nova Auto-Medicao");
        ComboBox<Obra> cbObra = comboObras();
        TextField tfPerc = campo("Percentagem *"), tfValor = campo("Valor");
        Label status = statusLabel();
        Button guardar = btnEstiloCor("Criar Auto-Medicao", C_ACCENT);
        guardar.setOnAction(e -> {
            try {
                Automedicao a = new Automedicao();
                a.setObraid(cbObra.getValue());
                a.setPercentagemtrabalho(new BigDecimal(tfPerc.getText().trim()));
                a.setValor(parse(tfValor.getText()));
                automedicaoService.criarAutomedicao(a);
                carregarAutomedicoes();
                status(status, true, "Auto-medicao criada.");
                limpar(tfPerc, tfValor);
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });
        Button aprovar = btnEstiloCor("Aprovar Selecionada", C_PRIMARY);
        aprovar.setOnAction(e -> {
            Automedicao sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Selecione uma auto-medicao."); return; }
            automedicaoService.aprovarAutomedicao(sel.getId());
            carregarAutomedicoes();
            status(status, true, "Auto-medicao aprovada.");
        });

        Button eliminarAuto = btnEstiloCor("Eliminar Selecionada", "#dc2626");
        eliminarAuto.setOnAction(e -> {
            Automedicao sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Selecione uma auto-medicao."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminação");
            confirm.setHeaderText(null);
            confirm.setContentText("Eliminar auto-medição #" + sel.getId() + "?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    automedicaoService.eliminarAutomedicao(sel.getId());
                    carregarAutomedicoes();
                    status(status, true, "Auto-medição eliminada.");
                }
            });
        });

        form.getChildren().addAll(
            label("Obra"), cbObra,
            label("Percentagem"), tfPerc,
            label("Valor"), tfValor,
            guardar, aprovar, eliminarAuto, status
        );
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarAutomedicoes));
    }

    private BorderPane criarPainelFaturas() {
        TableView<Fatura> tabela = new TableView<>(faturas);
        TableColumn<Fatura, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> new SimpleStringProperty(nomeCliente(c.getValue().getClienteid())));
        TableColumn<Fatura, String> obra = new TableColumn<>("Obra");
        obra.setCellValueFactory(c -> new SimpleStringProperty(idObra(c.getValue().getObraid())));
        TableColumn<Fatura, BigDecimal> valor = coluna("Valor","valor",120);
        configurarMoeda(valor);
        tabela.getColumns().addAll(
            coluna("ID","id",60), coluna("Numero","numerofatura",130),
            cliente, obra, coluna("Estado","estado",100), valor,
            coluna("Emissao","dataemissao",110), coluna("Vencimento","datavencimento",110)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox form = painelLateral("Emitir Fatura");
        ComboBox<Automedicao> cbAuto = comboAutomedicoes();
        // Número de fatura gerado automaticamente (editável)
        String proximoNum = String.format("FAT-%d-%04d", LocalDate.now().getYear(), faturas.size() + 1);
        TextField tfNum = campo("Numero *");
        tfNum.setText(proximoNum);
        TextField tfValor = campo("Valor *"),
                  tfIva = campo("IVA (%)"),   tfRet   = campo("Retencao (%)");
        // Preencher valor automaticamente ao selecionar auto-medição
        cbAuto.setOnAction(e -> {
            Automedicao am = cbAuto.getValue();
            if (am != null && am.getValor() != null && tfValor.getText().isBlank()) {
                tfValor.setText(am.getValor().toPlainString());
            }
        });
        DatePicker emissao    = new DatePicker(LocalDate.now());
        DatePicker vencimento = new DatePicker(LocalDate.now().plusDays(30));
        estilizarDatePicker(emissao); estilizarDatePicker(vencimento);
        Label status = statusLabel();
        Button emitir = btnEstiloCor("Emitir Fatura", C_ACCENT);
        emitir.setOnAction(e -> {
            Automedicao auto = cbAuto.getValue();
            if (auto == null) { status(status, false, "Selecione uma auto-medicao."); return; }
            try {
                Fatura f = new Fatura();
                f.setAutomedicaoid(auto);
                f.setObraid(auto.getObraid());
                f.setClienteid(auto.getObraid().getClienteid());
                f.setNumerofatura(tfNum.getText().trim());
                f.setValor(new BigDecimal(tfValor.getText().trim()));
                f.setDataemissao(emissao.getValue());
                f.setDatavencimento(vencimento.getValue());
                f.setIva(parseDefault(tfIva.getText(), new BigDecimal("23.00")));
                f.setRetencao(parseDefault(tfRet.getText(), BigDecimal.ZERO));
                faturaService.emitirFatura(f);
                carregarFaturas();
                status(status, true, "Fatura emitida.");
                limpar(tfNum, tfValor, tfIva, tfRet);
            } catch (Exception ex) { status(status, false, ex.getMessage()); }
        });
        Button pagar = btnEstiloCor("Marcar como Paga", C_PRIMARY);
        pagar.setOnAction(e -> {
            Fatura sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) { status(status, false, "Selecione uma fatura."); return; }
            faturaService.registarPagamento(sel.getId());
            carregarFaturas();
            status(status, true, "Fatura marcada como paga.");
        });
        // Resumo de faturação
        Separator sepRes = new Separator();
        Label lblRes = new Label("RESUMO DE FATURAÇÃO");
        lblRes.setStyle("-fx-font-size: 10; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + "; -fx-letter-spacing: 1.5;");
        Label lblResumoValores = new Label("—");
        lblResumoValores.setWrapText(true);
        lblResumoValores.setStyle("-fx-font-size: 12; -fx-text-fill: " + text() + ";");

        Button btnResumo = btnEstiloCor("Calcular Resumo", C_PRIMARY);
        btnResumo.setOnAction(e -> {
            BigDecimal totalPagas = faturas.stream()
                .filter(f -> "PAGA".equals(f.getEstado()))
                .map(f -> f.getValor() != null ? f.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPendentes = faturas.stream()
                .filter(f -> !"PAGA".equals(f.getEstado()))
                .map(f -> f.getValor() != null ? f.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            long nPagas = faturas.stream().filter(f -> "PAGA".equals(f.getEstado())).count();
            long nPendentes = faturas.size() - nPagas;
            lblResumoValores.setText(
                "✅ Pagas: " + nPagas + " fatura(s) = " + String.format("%.2f", totalPagas) + " €\n" +
                "⏳ Pendentes: " + nPendentes + " fatura(s) = " + String.format("%.2f", totalPendentes) + " €\n" +
                "📊 Total: " + faturas.size() + " fatura(s) = " + String.format("%.2f", totalPagas.add(totalPendentes)) + " €"
            );
        });

        form.getChildren().addAll(
            label("Auto-Medicao"), cbAuto,
            label("Numero"), tfNum,
            label("Valor"), tfValor,
            label("IVA (%)"), tfIva,
            label("Retencao (%)"), tfRet,
            label("Data de Emissao"), emissao,
            label("Data de Vencimento"), vencimento,
            emitir, pagar, status,
            sepRes, lblRes, btnResumo, lblResumoValores
        );
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarFaturas));
    }

    // ═══════════════════════════════════════════════════════════════
    //  PAINEL DE ESBOÇOS
    // ═══════════════════════════════════════════════════════════════

    private BorderPane criarPainelEsbocos() {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color: " + bg() + ";");
        painel.setPadding(new Insets(16, 16, 16, 16));

        // ── Canvas de desenho ────────────────────────────────────
        double canvasW = 1400, canvasH = 880;
        Canvas canvas = new Canvas(canvasW, canvasH);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        limparCanvas(gc, canvasW, canvasH);

        // ── Toolbar ──────────────────────────────────────────────
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(0, 0, 12, 0));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Selecionar obra
        Label lblObra = label("Obra:");
        ComboBox<Obra> cbObra = comboObras();
        cbObra.setMaxWidth(200);
        cbObra.setPromptText("Associar a obra...");

        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPrefHeight(32);

        // Ferramentas
        ToggleGroup toolGroup = new ToggleGroup();
        ToggleButton btnCaneta   = toggleFerramenta("✏  Caneta",   toolGroup, true);
        ToggleButton btnLinhas   = toggleFerramenta("╱  Linha",    toolGroup, false);
        ToggleButton btnBorracha = toggleFerramenta("◻  Borracha", toolGroup, false);

        Separator sep2 = new Separator(Orientation.VERTICAL);
        sep2.setPrefHeight(32);

        // Cor
        ColorPicker corPicker = new ColorPicker(Color.web("#1d1b16"));
        corPicker.setPrefWidth(72);
        corPicker.setStyle("-fx-color-label-visible: false;");
        Tooltip.install(corPicker, new Tooltip("Cor do traço"));

        // Espessura
        Label lblEsp = label("Esp.:");
        Slider sliderEsp = new Slider(1, 28, 3);
        sliderEsp.setPrefWidth(110);
        sliderEsp.setShowTickMarks(false);
        sliderEsp.setMajorTickUnit(9);
        Label lblEspVal = new Label("3px");
        lblEspVal.setStyle("-fx-font-size: 11; -fx-text-fill: " + textMuted() + "; -fx-min-width: 30;");
        sliderEsp.valueProperty().addListener((obs, o, n) ->
            lblEspVal.setText((int) n.doubleValue() + "px"));

        Separator sep3 = new Separator(Orientation.VERTICAL);
        sep3.setPrefHeight(32);

        // Botão cor de fundo
        Label lblFundo = label("Fundo:");
        ToggleButton btnFundoBranco = toggleSimples("Branco");
        ToggleButton btnFundoCreme  = toggleSimples("Creme");
        ToggleGroup fundoGroup = new ToggleGroup();
        btnFundoBranco.setToggleGroup(fundoGroup);
        btnFundoCreme.setToggleGroup(fundoGroup);
        btnFundoBranco.setSelected(true);
        final Color[] bgColorRef = {Color.WHITE};
        btnFundoBranco.setOnAction(e -> { limparCanvas(gc, canvasW, canvasH); bgColorRef[0] = Color.WHITE; });
        btnFundoCreme.setOnAction(e -> { limparCanvasCreme(gc, canvasW, canvasH); bgColorRef[0] = Color.web("#fdf8f0"); });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLimpar  = btnEstilo("Limpar", "#dc2626");
        btnLimpar.setMaxWidth(Region.USE_PREF_SIZE);
        Button btnGuardar = btnEstilo("  Guardar Esboço", C_ACCENT);
        btnGuardar.setMaxWidth(Region.USE_PREF_SIZE);

        toolbar.getChildren().addAll(
            lblObra, cbObra, sep1,
            btnCaneta, btnLinhas, btnBorracha, sep2,
            corPicker, lblEsp, sliderEsp, lblEspVal, sep3,
            lblFundo, btnFundoBranco, btnFundoCreme,
            spacer, btnLimpar, btnGuardar
        );

        // ── Eventos de desenho (rato) ────────────────────────────
        final double[] lastXY = {0, 0};

        canvas.setOnMousePressed(e -> {
            lastXY[0] = e.getX();
            lastXY[1] = e.getY();
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            aplicarEstiloTraco(gc, corPicker, sliderEsp, btnBorracha.isSelected(), bgColorRef[0]);
        });

        canvas.setOnMouseDragged(e -> {
            if (btnLinhas.isSelected()) {
                // Preview da linha: redesenhar não é trivial sem snapshot parcial,
                // então simplificamos: vai desenhando segmentos curtos
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.moveTo(e.getX(), e.getY());
            } else {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.moveTo(e.getX(), e.getY());
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (btnLinhas.isSelected()) {
                // Linha reta do ponto inicial ao ponto final
                gc.beginPath();
                aplicarEstiloTraco(gc, corPicker, sliderEsp, false, bgColorRef[0]);
                gc.moveTo(lastXY[0], lastXY[1]);
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });

        // ── Eventos de desenho (tátil / caneta) ─────────────────
        canvas.setOnTouchPressed(e -> {
            if (e.getTouchCount() == 1) {
                var tp = e.getTouchPoints().get(0);
                lastXY[0] = tp.getX(); lastXY[1] = tp.getY();
                gc.beginPath();
                gc.moveTo(tp.getX(), tp.getY());
                aplicarEstiloTraco(gc, corPicker, sliderEsp, btnBorracha.isSelected(), bgColorRef[0]);
                e.consume();
            }
        });
        canvas.setOnTouchMoved(e -> {
            if (e.getTouchCount() == 1) {
                var tp = e.getTouchPoints().get(0);
                gc.lineTo(tp.getX(), tp.getY());
                gc.stroke();
                gc.moveTo(tp.getX(), tp.getY());
                e.consume();
            }
        });

        // ── Limpar ───────────────────────────────────────────────
        btnLimpar.setOnAction(e -> {
            if (btnFundoCreme.isSelected()) limparCanvasCreme(gc, canvasW, canvasH);
            else limparCanvas(gc, canvasW, canvasH);
        });

        // ── Guardar como PNG ─────────────────────────────────────
        btnGuardar.setOnAction(e -> guardarEsboco(canvas, cbObra.getValue()));

        // ── ScrollPane do canvas ─────────────────────────────────
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(false);
        scroll.setFitToHeight(false);
        scroll.setPannable(false); // arrastar = desenhar, não fazer pan
        scroll.setStyle(
            "-fx-background-color: #d8d2c8; -fx-background: #d8d2c8;"
          + "-fx-padding: 12;"
        );
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // ── Barra de status ──────────────────────────────────────
        Label lblStatus = new Label("  Pronto para desenhar. Usa o rato ou caneta para esboçar.");
        lblStatus.setStyle("-fx-font-size: 11; -fx-text-fill: " + textMuted() + "; -fx-padding: 6 0 0 0;");
        canvas.setOnMouseMoved(e ->
            lblStatus.setText(String.format("  x: %.0f  y: %.0f", e.getX(), e.getY()))
        );

        painel.setTop(toolbar);
        painel.setCenter(scroll);
        painel.setBottom(lblStatus);
        return painel;
    }

    private void aplicarEstiloTraco(GraphicsContext gc, ColorPicker cor, Slider esp, boolean borracha, Color corFundo) {
        if (borracha) {
            gc.setStroke(corFundo != null ? corFundo : Color.WHITE);
            gc.setLineWidth(esp.getValue() * 5);
        } else {
            gc.setStroke(cor.getValue());
            gc.setLineWidth(esp.getValue());
        }
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
    }

    private void limparCanvas(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);
        desenharGrelha(gc, w, h, "#f0ece4");
    }

    private void limparCanvasCreme(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.web("#fdf8f0"));
        gc.fillRect(0, 0, w, h);
        desenharGrelha(gc, w, h, "#e8e0d4");
    }

    private void desenharGrelha(GraphicsContext gc, double w, double h, String cor) {
        gc.setStroke(Color.web(cor));
        gc.setLineWidth(0.8);
        for (double x = 0; x <= w; x += 40) gc.strokeLine(x, 0, x, h);
        for (double y = 0; y <= h; y += 40) gc.strokeLine(0, y, w, y);
    }

    private void guardarEsboco(Canvas canvas, Obra obra) {
        if (obra == null) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Obra não selecionada");
            aviso.setHeaderText(null);
            aviso.setContentText("Seleciona uma obra no menu antes de guardar o esboço.");
            aviso.showAndWait();
            return;
        }
        try {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.WHITE);
            WritableImage wimg = canvas.snapshot(params, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(wimg, null), "png", baos);
            byte[] bytes = baos.toByteArray();

            String nome = "Esboço Obra #" + obra.getId() + " — "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            esbocoService.guardarEsboco(obra, nome, bytes);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Esboço guardado");
            ok.setHeaderText(null);
            ok.setContentText("Esboço guardado na base de dados!\n" + nome);
            ok.showAndWait();
        } catch (Exception ex) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Erro ao guardar");
            err.setHeaderText(null);
            err.setContentText("Erro: " + ex.getMessage());
            err.showAndWait();
        }
    }

    private ToggleButton toggleFerramenta(String txt, ToggleGroup grp, boolean sel) {
        ToggleButton btn = new ToggleButton(txt);
        btn.setToggleGroup(grp);
        btn.setSelected(sel);
        btn.setStyle(estiloToggle(sel));
        btn.selectedProperty().addListener((obs, o, n) -> btn.setStyle(estiloToggle(n)));
        return btn;
    }

    private ToggleButton toggleSimples(String txt) {
        ToggleButton btn = new ToggleButton(txt);
        btn.setStyle(estiloToggle(false));
        btn.selectedProperty().addListener((obs, o, n) -> btn.setStyle(estiloToggle(n)));
        return btn;
    }

    private String estiloToggle(boolean sel) {
        return sel
            ? "-fx-background-color: " + C_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: 700;"
            + "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand;"
            : "-fx-background-color: " + surface() + "; -fx-text-fill: " + text() + "; -fx-font-weight: 600;"
            + "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand;"
            + "-fx-border-color: " + border() + "; -fx-border-radius: 8;";
    }

    private ScrollPane criarPainelCustos() {
        VBox painel = new VBox(20);
        painel.setPadding(new Insets(28));
        painel.setStyle("-fx-background-color: " + bg() + ";");

        Label titulo = new Label("Resumo de Custos por Obra");
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: 900; -fx-text-fill: " + primary() + ";");
        Label instrucao = subtitulo("Escolhe uma obra e clica em Calcular para ver o resumo.");

        Label lblObra = label("Selecionar Obra:");
        ComboBox<Obra> cbObra = comboObras();
        cbObra.setMaxWidth(400);
        cbObra.setPromptText("Escolhe uma obra...");
        // Pré-selecionar sem disparar cálculo
        if (obraSelecionada != null) cbObra.getSelectionModel().select(obraSelecionada);

        // Área de resultados — separada do painel principal
        VBox resultados = new VBox(16);

        Button calcular = btnEstilo("📊  Calcular Custos", C_PRIMARY);
        calcular.setOnAction(e -> {
            Obra sel = cbObra.getValue();
            if (sel == null) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("Seleciona uma obra primeiro.");
                a.showAndWait();
                return;
            }
            obraSelecionada = sel;
            resultados.getChildren().clear();
            mostrarResultadosCustos(resultados, sel);
        });

        Button imprimir = btnEstilo("🖨  Imprimir Relatório", "#475569");
        imprimir.setOnAction(e -> {
            Obra sel = cbObra.getValue();
            if (sel == null) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("Seleciona uma obra primeiro.");
                a.showAndWait();
                return;
            }
            obraSelecionada = sel;
            imprimirRelatorio(painel);
        });

        HBox btnRow = new HBox(12, calcular, imprimir);

        painel.getChildren().addAll(titulo, instrucao, lblObra, cbObra, btnRow, resultados);

        ScrollPane scroll = new ScrollPane(painel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + bg() + "; -fx-background: " + bg() + ";");
        return scroll;
    }

    private void mostrarResultadosCustos(VBox resultados, Obra obra) {
        BigDecimal mat   = materialService.custoTotalMateriais(obra.getId());
        BigDecimal mdo   = maodeobraService.custoTotalMaodeobra(obra.getId());
        BigDecimal total = mat.add(mdo);
        BigDecimal orc   = obra.getPropostaid() == null ? BigDecimal.ZERO : obra.getPropostaid().getValortotal();
        BigDecimal desvio = total.subtract(orc);

        Label obraLabel = subtitulo("Obra " + idObra(obra) + "  ·  " + obra.getEstado()
            + "  ·  " + obra.getPercentagemconclusao() + "% concluída");
        obraLabel.setStyle("-fx-font-size: 13; -fx-text-fill: " + textMuted() + ";");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(16);
        grid.add(card("Orcamento Previsto",  orc,   C_PRIMARY),  0, 0);
        grid.add(card("Custo de Materiais",  mat,   C_ACCENT),   1, 0);
        grid.add(card("Mao de Obra",         mdo,   "#4b6480"),  2, 0);
        grid.add(card("Total Real",          total, "#112b46"),  0, 1);

        String desvioCor = desvio.compareTo(BigDecimal.ZERO) > 0 ? "#dc2626" : "#16a34a";
        String desvioTxt = (desvio.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + String.format("%.2f EUR", desvio);
        VBox desvioCard = new VBox(6);
        desvioCard.setPadding(new Insets(18, 18, 18, 22));
        desvioCard.setPrefWidth(240);
        desvioCard.setStyle("-fx-background-color: " + surface() + "; -fx-background-radius: 12;"
            + "-fx-border-color: transparent transparent transparent " + desvioCor + ";"
            + "-fx-border-width: 0 0 0 4; -fx-border-radius: 12;"
            + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.10),16,0,0,3);");
        Label dl1 = new Label("DESVIO (REAL - ORÇAMENTO)");
        dl1.setStyle("-fx-font-size: 10; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + ";");
        Label dl2 = new Label(desvioTxt);
        dl2.setStyle("-fx-font-size: 20; -fx-font-weight: 800; -fx-text-fill: " + desvioCor + ";");
        desvioCard.getChildren().addAll(dl1, dl2);
        grid.add(desvioCard, 1, 1);

        resultados.getChildren().addAll(obraLabel, grid);
    }

    private void calcularCustos(VBox painel) {
        // Mantido apenas para compatibilidade com imprimirRelatorio
        // O painel Custos agora usa mostrarResultadosCustos diretamente
    }

    private void imprimirRelatorio(VBox painelCustos) {
        if (obraSelecionada == null) return;

        BigDecimal mat   = materialService.custoTotalMateriais(obraSelecionada.getId());
        BigDecimal mdo   = maodeobraService.custoTotalMaodeobra(obraSelecionada.getId());
        BigDecimal total = mat.add(mdo);
        BigDecimal orc   = obraSelecionada.getPropostaid() == null
                         ? BigDecimal.ZERO
                         : obraSelecionada.getPropostaid().getValortotal();
        BigDecimal desvio = total.subtract(orc);

        // Construir node para impressão
        VBox relatorio = new VBox(14);
        relatorio.setPadding(new Insets(40));
        relatorio.setStyle("-fx-background-color: white;");
        relatorio.setPrefWidth(680);

        Label titRel = new Label("DS CONSTRUÇÕES — RELATÓRIO DE CUSTOS");
        titRel.setStyle("-fx-font-size: 18; -fx-font-weight: 900; -fx-text-fill: #00294f;");
        Label linha = new Label("─".repeat(68));
        linha.setStyle("-fx-text-fill: #aaa;");
        Label obraInfo = new Label("Obra: " + idObra(obraSelecionada)
            + "   |   Cliente: " + nomeCliente(obraSelecionada.getClienteid())
            + "   |   Estado: " + obraSelecionada.getEstado()
            + "   |   " + obraSelecionada.getPercentagemconclusao() + "% concluída");
        obraInfo.setStyle("-fx-font-size: 12; -fx-text-fill: #333;");
        Label dataRel = new Label("Data do relatório: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataRel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");

        Label linha2 = new Label("─".repeat(68));
        linha2.setStyle("-fx-text-fill: #aaa;");

        GridPane tabela = new GridPane();
        tabela.setHgap(40); tabela.setVgap(10);

        Label[] headers = { new Label("DESCRIÇÃO"), new Label("VALOR") };
        for (Label h : headers) h.setStyle("-fx-font-size: 11; -fx-font-weight: 700; -fx-text-fill: #666;");
        tabela.add(headers[0], 0, 0); tabela.add(headers[1], 1, 0);

        String[][] linhas = {
            { "Orçamento Previsto (Proposta)", String.format("%.2f €", orc) },
            { "Custo de Materiais",            String.format("%.2f €", mat) },
            { "Custo de Mão de Obra",          String.format("%.2f €", mdo) },
            { "Total Real",                    String.format("%.2f €", total) },
            { "Desvio (Real - Orçamento)",     (desvio.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + String.format("%.2f €", desvio) }
        };
        for (int i = 0; i < linhas.length; i++) {
            Label desc = new Label(linhas[i][0]);
            desc.setStyle("-fx-font-size: 13; -fx-text-fill: #222;");
            Label val = new Label(linhas[i][1]);
            boolean desvioLinha = i == 4;
            String cor = desvioLinha
                ? (desvio.compareTo(BigDecimal.ZERO) > 0 ? "#dc2626" : "#16a34a")
                : "#00294f";
            val.setStyle("-fx-font-size: 13; -fx-font-weight: 700; -fx-text-fill: " + cor + ";");
            tabela.add(desc, 0, i + 1);
            tabela.add(val, 1, i + 1);
        }

        Label rodape = new Label("Relatório gerado automaticamente por DS Construções — Sistema de Gestão de Empreitadas");
        rodape.setStyle("-fx-font-size: 10; -fx-text-fill: #aaa;");

        relatorio.getChildren().addAll(titRel, linha, obraInfo, dataRel, linha2, tabela, new Region(), rodape);

        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job != null) {
            boolean ok = job.showPrintDialog(null);
            if (ok) {
                javafx.print.PageLayout layout = job.getJobSettings().getPageLayout();
                relatorio.setPrefWidth(layout.getPrintableWidth());
                relatorio.autosize();
                if (job.printPage(relatorio)) {
                    job.endJob();
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText(null);
                    info.setContentText("Relatório enviado para impressão!");
                    info.showAndWait();
                }
            }
        }
    }

    private VBox card(String lbl, BigDecimal valor, String cor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 18, 18, 22));
        card.setPrefWidth(240);
        card.setStyle("-fx-background-color: " + surface() + ";"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: transparent transparent transparent " + cor + ";"
                    + "-fx-border-width: 0 0 0 4;"
                    + "-fx-border-radius: 12;"
                    + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.10),16,0,0,3);");
        Label l1 = new Label(lbl.toUpperCase());
        l1.setStyle("-fx-font-size: 10; -fx-font-weight: 700; -fx-text-fill: " + textMuted() + "; -fx-letter-spacing: 1;");
        Label l2 = new Label(String.format("%.2f EUR", valor));
        l2.setStyle("-fx-font-size: 20; -fx-font-weight: 800; -fx-text-fill: " + cor + ";");
        card.getChildren().addAll(l1, l2);
        return card;
    }

    // ═══════════════════════════════════════════════════════════════
    //  DATA LOADING
    // ═══════════════════════════════════════════════════════════════

    private void carregarDados() {
        carregarClientes(); carregarFuncionarios(); carregarPropostas();
        carregarObras(); carregarAutomedicoes(); carregarFaturas();
        carregarMateriais(); carregarMaodeobra(); atualizarObraSelecionada();
    }

    private void carregarClientes()    { clientes.setAll(clienteService.listarTodos()); }
    private void carregarFuncionarios(){ funcionarios.setAll(funcionarioService.listarTodos()); }
    private void carregarPropostas()   { propostas.setAll(propostaService.listarTodas()); }
    private void carregarObras()       { obras.setAll(obraService.listarTodas()); }
    private void carregarAutomedicoes(){ automedicoes.setAll(automedicaoService.listarTodas()); }
    private void carregarFaturas()     { faturas.setAll(faturaService.listarTodas()); }
    private void carregarMateriais()   {
        materiais.clear();
        if (obraSelecionada != null) materiais.addAll(materialService.listarPorObra(obraSelecionada.getId()));
    }
    private void carregarMaodeobra()   {
        maodeobra.clear();
        if (obraSelecionada != null) maodeobra.addAll(maodeobraService.listarPorObra(obraSelecionada.getId()));
    }

    private void atualizarObraSelecionada() {
        String txt = obraSelecionada == null
            ? "Selecione uma obra no separador Obras primeiro."
            : "Obra: " + idObra(obraSelecionada) + "  |  " + obraSelecionada.getEstado()
              + "  |  " + obraSelecionada.getPercentagemconclusao() + "%";
        if (obraInfoMateriais != null) obraInfoMateriais.setText(txt);
        if (obraInfoMaoDeObra != null) obraInfoMaoDeObra.setText(txt);
    }

    // ═══════════════════════════════════════════════════════════════
    //  UI HELPERS
    // ═══════════════════════════════════════════════════════════════

    private BorderPane painelPrincipal(TableView<?> tabela, VBox form, Button refresh) {
        BorderPane painel = new BorderPane();
        painel.setPadding(new Insets(20, 20, 20, 20));
        painel.setStyle("-fx-background-color: " + bg() + ";");

        tabela.setStyle(
            "-fx-background-color: " + surface() + ";"
          + "-fx-control-inner-background: " + surface() + ";"
          + "-fx-background-insets: 0;"
          + "-fx-border-color: transparent;"
          + "-fx-border-radius: 12;"
          + "-fx-background-radius: 12;"
          + "-fx-table-cell-border-color: transparent;"
          + "-fx-accent: " + primary() + ";"
          + "-fx-focus-color: transparent;"
          + "-fx-faint-focus-color: transparent;"
        );

        HBox topo = new HBox(10, refresh);
        topo.setPadding(new Insets(0, 0, 12, 0));
        topo.setAlignment(Pos.CENTER_LEFT);

        painel.setTop(topo);
        painel.setCenter(tabela);
        painel.setRight(form);
        BorderPane.setMargin(form, new Insets(0, 0, 0, 16));
        return painel;
    }

    private VBox painelLateral(String titulo) {
        VBox box = new VBox(10);
        box.setPrefWidth(320);
        box.setPadding(new Insets(18));
        box.setStyle(
            "-fx-background-color: " + surface() + ";"
          + "-fx-background-radius: 12;"
          + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.10),18,0,0,3);"
        );
        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-size: 14; -fx-font-weight: 800; -fx-text-fill: " + primary() + ";");
        Label subLabel = new Label("Preencha os campos abaixo");
        subLabel.setStyle("-fx-font-size: 11; -fx-text-fill: " + textMuted() + ";");
        Separator sep = new Separator();
        box.getChildren().addAll(tituloLabel, subLabel, sep);
        return box;
    }

    private <T, V> TableColumn<T, V> coluna(String nome, String prop, int largura) {
        TableColumn<T, V> c = new TableColumn<>(nome);
        c.setPrefWidth(largura);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private <T> void configurarMoeda(TableColumn<T, BigDecimal> coluna) {
        coluna.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f EUR", item));
            }
        });
    }

    // ── Label helpers
    private Label subtitulo(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-size: 12; -fx-text-fill: " + textMuted() + ";");
        return l;
    }
    private Label label(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-size: 13; -fx-font-weight: 600; -fx-text-fill: " + text() + ";");
        return l;
    }
    private Label statusLabel() {
        Label l = new Label();
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 12;");
        return l;
    }

    // ── Field helpers
    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: " + surfaceLow() + "; -fx-text-fill: " + text()
                  + "; -fx-border-color: " + border() + "; -fx-border-radius: 10; -fx-background-radius: 10;"
                  + "-fx-padding: 9 12; -fx-font-size: 13;");
        return tf;
    }
    private TextArea area(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(3);
        ta.setWrapText(true);
        ta.setStyle("-fx-background-color: " + surfaceLow() + "; -fx-text-fill: " + text()
                  + "; -fx-border-color: " + border() + "; -fx-border-radius: 10; -fx-background-radius: 10;"
                  + "-fx-font-size: 13;");
        return ta;
    }

    // ── Button helpers
    private Button btnEstiloCor(String texto, String cor) {
        Button b = new Button(texto);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(estiloBtn(cor));
        return b;
    }
    private Button btnEstilo(String texto, String cor) {
        Button b = new Button(texto);
        b.setStyle(estiloBtn(cor));
        return b;
    }
    private String estiloBtn(String cor) {
        return "-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-weight: 700;"
             + "-fx-padding: 10 18; -fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 13;";
    }
    private Button botaoAtualizar(Runnable fn) {
        Button b = btnEstiloCor("Atualizar", C_PRIMARY);
        b.setMaxWidth(Region.USE_PREF_SIZE);
        b.setOnAction(e -> fn.run());
        return b;
    }

    // ── Combo helpers
    private ComboBox<String> comboTexto(String... valores) {
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(valores));
        cb.setMaxWidth(Double.MAX_VALUE);
        if (valores.length > 0) cb.setValue(valores[0]);
        estilizarCombo(cb);
        return cb;
    }
    private ComboBox<Cliente> comboClientes() {
        ComboBox<Cliente> cb = new ComboBox<>(clientes);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Cliente i, boolean e) { super.updateItem(i,e); setText(e||i==null?"":" #"+i.getId()+" - "+i.getNome()); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Cliente i, boolean e) { super.updateItem(i,e); setText(e||i==null?"Selecionar cliente...":i.getNome()); }
        });
        return cb;
    }
    private ComboBox<Proposta> comboPropostas() {
        ComboBox<Proposta> cb = new ComboBox<>(propostas);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Proposta i, boolean e) { super.updateItem(i,e); setText(e||i==null?"":" #"+i.getId()+" - "+nomeCliente(i.getClienteid())); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Proposta i, boolean e) { super.updateItem(i,e); setText(e||i==null?"Selecionar proposta...":"#"+i.getId()); }
        });
        return cb;
    }
    private ComboBox<Obra> comboObras() {
        ComboBox<Obra> cb = new ComboBox<>(obras);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Obra i, boolean e) { super.updateItem(i,e); setText(e||i==null?"":idObra(i)+" - "+nomeCliente(i.getClienteid())); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Obra i, boolean e) { super.updateItem(i,e); setText(e||i==null?"Selecionar obra...":idObra(i)); }
        });
        return cb;
    }
    private ComboBox<Funcionario> comboFuncionarios() {
        ComboBox<Funcionario> cb = new ComboBox<>(funcionarios);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Funcionario i, boolean e) { super.updateItem(i,e); setText(e||i==null?"":i.getNome()+" ("+i.getCargo()+")"); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Funcionario i, boolean e) { super.updateItem(i,e); setText(e||i==null?"Selecionar funcionario...":i.getNome()); }
        });
        return cb;
    }
    private ComboBox<Automedicao> comboAutomedicoes() {
        ComboBox<Automedicao> cb = new ComboBox<>(automedicoes);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Automedicao i, boolean e) { super.updateItem(i,e); setText(e||i==null?"":" #"+i.getId()+" - "+idObra(i.getObraid())); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Automedicao i, boolean e) { super.updateItem(i,e); setText(e||i==null?"Selecionar auto-medicao...":"#"+i.getId()); }
        });
        return cb;
    }
    private void estilizarCombo(ComboBox<?> cb) {
        cb.setStyle("-fx-background-color: " + surfaceLow() + "; -fx-border-color: " + border() + ";"
                  + "-fx-border-radius: 10; -fx-background-radius: 10; -fx-font-size: 13;");
    }
    private void estilizarDatePicker(DatePicker dp) {
        dp.setStyle("-fx-background-color: " + surfaceLow() + "; -fx-border-color: " + border() + ";"
                  + "-fx-border-radius: 10; -fx-background-radius: 10;");
        dp.setMaxWidth(Double.MAX_VALUE);
    }

    // ── Logo helpers
    private void aplicarIcone(Stage stage) {
        Image logo = carregarLogo();
        if (logo != null) stage.getIcons().add(logo);
    }
    private ImageView criarLogoView(double altura) {
        Image logo = carregarLogo();
        if (logo == null) return null;
        ImageView iv = new ImageView(logo);
        Rectangle2D vp = cropLogoViewport(logo);
        if (vp != null) iv.setViewport(vp);
        iv.setPreserveRatio(true);
        iv.setFitHeight(altura);
        iv.setSmooth(true);
        return iv;
    }
    private Rectangle2D cropLogoViewport(Image logo) {
        double w = logo.getWidth(), h = logo.getHeight();
        if (w <= 0 || h <= 0) return null;
        return new Rectangle2D(w*0.14, h*0.10, w*0.72, h*0.72);
    }
    private Image carregarLogo() {
        var res = DesktopApp.class.getResource("/static/assets/ds-construcoes-logo.png");
        return res == null ? null : new Image(res.toExternalForm());
    }

    // ── Misc helpers
    private Region dividerH() {
        Region d = new Region();
        d.setPrefHeight(1);
        d.setMaxWidth(Double.MAX_VALUE);
        d.setStyle("-fx-background-color: rgba(255,255,255,0.10);");
        return d;
    }
    private String idObra(Obra obra) {
        return obra == null || obra.getId() == null ? "-" : "#" + obra.getId();
    }
    private String nomeCliente(Cliente c) {
        Integer id = c == null ? null : c.getId();
        if (id == null) return "-";
        for (Cliente cl : clientes) if (id.equals(cl.getId())) return cl.getNome();
        return "#" + id;
    }
    private String nomeFuncionario(Funcionario f) {
        Integer id = f == null ? null : f.getId();
        if (id == null) return "-";
        for (Funcionario fn : funcionarios) if (id.equals(fn.getId())) return fn.getNome();
        return "#" + id;
    }
    private BigDecimal parse(String v) {
        return v == null || v.isBlank() ? BigDecimal.ZERO : new BigDecimal(v.trim());
    }
    private BigDecimal parseDefault(String v, BigDecimal fallback) {
        return v == null || v.isBlank() ? fallback : new BigDecimal(v.trim());
    }
    private void limpar(TextField... campos) { for (TextField f : campos) f.clear(); }
    private void status(Label lbl, boolean ok, String txt) {
        lbl.setStyle("-fx-font-size: 12; -fx-text-fill: " + (ok ? "#16a34a" : "#dc2626") + ";");
        lbl.setText(txt == null || txt.isBlank() ? "Operacao invalida." : txt);
    }
}
