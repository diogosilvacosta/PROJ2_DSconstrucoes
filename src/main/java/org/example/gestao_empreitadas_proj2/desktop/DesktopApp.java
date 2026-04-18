package org.example.gestao_empreitadas_proj2.desktop;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.gestao_empreitadas_proj2.models.Automedicao;
import org.example.gestao_empreitadas_proj2.models.Cliente;
import org.example.gestao_empreitadas_proj2.models.Fatura;
import org.example.gestao_empreitadas_proj2.models.Funcionario;
import org.example.gestao_empreitadas_proj2.models.Obra;
import org.example.gestao_empreitadas_proj2.models.ObraMaodeobra;
import org.example.gestao_empreitadas_proj2.models.ObraMaterial;
import org.example.gestao_empreitadas_proj2.models.Proposta;
import org.example.gestao_empreitadas_proj2.services.AutomedicaoService;
import org.example.gestao_empreitadas_proj2.services.ClienteService;
import org.example.gestao_empreitadas_proj2.services.FaturaService;
import org.example.gestao_empreitadas_proj2.services.FuncionarioService;
import org.example.gestao_empreitadas_proj2.services.ObraMaodeobraService;
import org.example.gestao_empreitadas_proj2.services.ObraMaterialService;
import org.example.gestao_empreitadas_proj2.services.ObraService;
import org.example.gestao_empreitadas_proj2.services.PropostaService;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DesktopApp extends Application {
    private static final String COLOR_BG = "#f6f0e7";
    private static final String COLOR_SURFACE = "#fffaf5";
    private static final String COLOR_SURFACE_ALT = "#f2e8dc";
    private static final String COLOR_BORDER = "#ddd2c2";
    private static final String COLOR_PRIMARY = "#173f6b";
    private static final String COLOR_PRIMARY_DARK = "#112b46";
    private static final String COLOR_MUTED = "#6a7788";
    private static final String COLOR_TEXT = "#213247";

    private static ConfigurableApplicationContext springCtx;

    private ClienteService clienteService;
    private PropostaService propostaService;
    private ObraService obraService;
    private AutomedicaoService automedicaoService;
    private FaturaService faturaService;
    private ObraMaterialService materialService;
    private ObraMaodeobraService maodeobraService;
    private FuncionarioService funcionarioService;

    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    private final ObservableList<Proposta> propostas = FXCollections.observableArrayList();
    private final ObservableList<Obra> obras = FXCollections.observableArrayList();
    private final ObservableList<Automedicao> automedicoes = FXCollections.observableArrayList();
    private final ObservableList<Fatura> faturas = FXCollections.observableArrayList();
    private final ObservableList<ObraMaterial> materiais = FXCollections.observableArrayList();
    private final ObservableList<ObraMaodeobra> maodeobra = FXCollections.observableArrayList();
    private final ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList();

    private Obra obraSelecionada;
    private Label obraInfoMateriais;
    private Label obraInfoMaoDeObra;
    private BorderPane mainRoot;
    private boolean isDarkMode = false;

    public static void setContext(ConfigurableApplicationContext ctx) {
        springCtx = ctx;
    }

    @Override
    public void init() {
        if (springCtx == null) throw new IllegalStateException("Use DesktopLauncher.");
        clienteService = springCtx.getBean(ClienteService.class);
        propostaService = springCtx.getBean(PropostaService.class);
        obraService = springCtx.getBean(ObraService.class);
        automedicaoService = springCtx.getBean(AutomedicaoService.class);
        faturaService = springCtx.getBean(FaturaService.class);
        materialService = springCtx.getBean(ObraMaterialService.class);
        maodeobraService = springCtx.getBean(ObraMaodeobraService.class);
        funcionarioService = springCtx.getBean(FuncionarioService.class);
    }

    @Override
    public void start(Stage stage) {
        mainRoot = new BorderPane();
        mainRoot.setTop(criarHeader());
        mainRoot.setCenter(criarTabs());
        carregarDados();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double largura = Math.min(1360, Math.max(980, bounds.getWidth() * 0.92));
        double altura = Math.min(820, Math.max(700, bounds.getHeight() * 0.9));
        stage.setTitle("DS Construcoes | Gestao Interna");
        aplicarIcone(stage);
        
        Scene scene = new Scene(mainRoot, largura, altura);
        var css = DesktopApp.class.getResource("/desktop-style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        if (springCtx != null) springCtx.close();
    }

    private TabPane criarTabs() {
        TabPane tabs = new TabPane(
            new Tab("Clientes", criarPainelClientes()),
            new Tab("Propostas", criarPainelPropostas()),
            new Tab("Obras", criarPainelObras()),
            new Tab("Materiais", criarPainelMateriais()),
            new Tab("Mao de Obra", criarPainelMaodeobra()),
            new Tab("Auto-medicoes", criarPainelAutomedicoes()),
            new Tab("Faturas", criarPainelFaturas()),
            new Tab("Custos", criarPainelCustos())
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        return tabs;
    }

    private HBox criarHeader() {
        HBox header = new HBox(16);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        
        HBox brand = new HBox(14);
        brand.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = criarLogoView(82);
        VBox brandInfo = new VBox(3, titulo("DS Construcoes", 22), subtitulo("Gestao interna de empreitadas"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        if (logo != null) {
            brand.getChildren().add(logo);
        }
        brand.getChildren().add(brandInfo);

        Button btnTheme = new Button("Night Mode");
        btnTheme.setStyle("-fx-background-color: #173f6b; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20;");
        btnTheme.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            if (isDarkMode) {
                mainRoot.getStyleClass().add("dark");
                btnTheme.setText("Light Mode");
                btnTheme.setStyle("-fx-background-color: #ef6b2e; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20;");
            } else {
                mainRoot.getStyleClass().remove("dark");
                btnTheme.setText("Night Mode");
                btnTheme.setStyle("-fx-background-color: #173f6b; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20;");
            }
        });

        header.getChildren().addAll(brand, spacer, btnTheme, subtitulo("Desktop v1"));
        return header;
    }

    private void aplicarIcone(Stage stage) {
        Image logo = carregarLogo();
        if (logo != null) {
            stage.getIcons().add(logo);
        }
    }

    private ImageView criarLogoView(double altura) {
        Image logo = carregarLogo();
        if (logo == null) {
            return null;
        }
        ImageView imageView = new ImageView(logo);
        Rectangle2D viewport = cropLogoViewport(logo);
        if (viewport != null) {
            imageView.setViewport(viewport);
        }
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(altura);
        imageView.setSmooth(true);
        return imageView;
    }

    private Rectangle2D cropLogoViewport(Image logo) {
        double width = logo.getWidth();
        double height = logo.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        double x = width * 0.14;
        double y = height * 0.10;
        double croppedWidth = width * 0.72;
        double croppedHeight = height * 0.72;
        return new Rectangle2D(x, y, croppedWidth, croppedHeight);
    }

    private Image carregarLogo() {
        var resource = DesktopApp.class.getResource("/static/assets/ds-construcoes-logo.png");
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private BorderPane criarPainelClientes() {
        TableView<Cliente> tabela = new TableView<>(clientes);
        tabela.getColumns().addAll(coluna("ID", "id", 60), coluna("Nome", "nome", 220), coluna("Email", "email", 220), coluna("NIF", "nif", 100), coluna("Telefone", "telefone", 120));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox form = painelLateral("Novo Cliente");
        TextField nome = campo("Nome *"), email = campo("Email *"), nif = campo("NIF *"), telefone = campo("Telefone"), morada = campo("Morada");
        Label status = statusLabel();
        Button guardar = botao("Registar Cliente", "#ef6b2e");
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
                status(status, true, "Cliente registado.");
                limpar(nome, email, nif, telefone, morada);
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        form.getChildren().addAll(label("Nome"), nome, label("Email"), email, label("NIF"), nif, label("Telefone"), telefone, label("Morada"), morada, guardar, status);
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarClientes));
    }

    private BorderPane criarPainelPropostas() {
        TableView<Proposta> tabela = new TableView<>(propostas);
        TableColumn<Proposta, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> new SimpleStringProperty(nomeCliente(c.getValue().getClienteid())));
        TableColumn<Proposta, BigDecimal> valor = coluna("Valor", "valortotal", 120);
        configurarMoeda(valor);
        tabela.getColumns().addAll(coluna("ID", "id", 60), cliente, coluna("Estado", "estado", 100), coluna("Data", "dataproposta", 120), valor);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox form = painelLateral("Nova Proposta");
        ComboBox<Cliente> cbCliente = comboClientes();
        ComboBox<String> cbEstado = comboTexto("RASCUNHO", "ENVIADA", "ACEITE", "REJEITADA");
        TextField tfValor = campo("Valor total *");
        TextArea taDesc = area("Descricao");
        Label status = statusLabel();
        Button guardar = botao("Criar Proposta", "#ef6b2e");
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
                tfValor.clear();
                taDesc.clear();
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        form.getChildren().addAll(label("Cliente"), cbCliente, label("Estado"), cbEstado, label("Valor"), tfValor, label("Descricao"), taDesc, guardar, status);
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarPropostas));
    }

    private BorderPane criarPainelObras() {
        TableView<Obra> tabela = new TableView<>(obras);
        TableColumn<Obra, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> new SimpleStringProperty(nomeCliente(c.getValue().getClienteid())));
        TableColumn<Obra, String> proposta = new TableColumn<>("Proposta");
        proposta.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPropostaid() == null ? "-" : "#" + c.getValue().getPropostaid().getId()));
        tabela.getColumns().addAll(coluna("ID", "id", 60), cliente, proposta, coluna("Estado", "estado", 120), coluna("Data", "datainicio", 120), coluna("%", "percentagemconclusao", 80));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            obraSelecionada = newValue;
            atualizarObraSelecionada();
            carregarMateriais();
            carregarMaodeobra();
        });
        VBox form = painelLateral("Nova Obra");
        ComboBox<Cliente> cbCliente = comboClientes();
        ComboBox<Proposta> cbProposta = comboPropostas();
        ComboBox<String> cbEstado = comboTexto("INICIADA", "EM_PROGRESSO", "SUSPENSA", "CONCLUIDA");
        TextField tfPerc = campo("Percentagem");
        Label status = statusLabel();
        Button guardar = botao("Criar Obra", "#ef6b2e");
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
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        form.getChildren().addAll(label("Cliente"), cbCliente, label("Proposta"), cbProposta, label("Estado"), cbEstado, label("Percentagem"), tfPerc, guardar, status);
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarObras));
    }

    private BorderPane criarPainelMateriais() {
        TableView<ObraMaterial> tabela = new TableView<>(materiais);
        TableColumn<ObraMaterial, BigDecimal> preco = coluna("Preco", "precoUnitario", 100);
        TableColumn<ObraMaterial, BigDecimal> total = new TableColumn<>("Total");
        total.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCustoTotal()));
        configurarMoeda(preco);
        configurarMoeda(total);
        tabela.getColumns().addAll(coluna("Descricao", "descricao", 240), coluna("Unidade", "unidade", 90), coluna("Quantidade", "quantidade", 100), preco, total);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        obraInfoMateriais = subtitulo("Selecione uma obra no separador Obras primeiro.");
        VBox form = painelLateral("Adicionar Material");
        TextField desc = campo("Descricao *"), un = campo("Unidade"), qtd = campo("Quantidade *"), precoTf = campo("Preco *");
        Label status = statusLabel();
        Button guardar = botao("Adicionar Material", "#ef6b2e");
        guardar.setOnAction(e -> {
            if (obraSelecionada == null) {
                status(status, false, "Selecione uma obra.");
                return;
            }
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
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        form.getChildren().addAll(label("Descricao"), desc, label("Unidade"), un, label("Quantidade"), qtd, label("Preco"), precoTf, guardar, status);
        BorderPane painel = painelPrincipal(tabela, form, botaoAtualizar(this::carregarMateriais));
        painel.setTop(new VBox(6, obraInfoMateriais, new HBox(10, botaoAtualizar(this::carregarMateriais))));
        return painel;
    }

    private BorderPane criarPainelMaodeobra() {
        TableView<ObraMaodeobra> tabela = new TableView<>(maodeobra);
        TableColumn<ObraMaodeobra, String> func = new TableColumn<>("Funcionario");
        func.setCellValueFactory(c -> new SimpleStringProperty(nomeFuncionario(c.getValue().getFuncionarioid())));
        TableColumn<ObraMaodeobra, BigDecimal> custo = coluna("EUR/h", "custoHora", 90);
        TableColumn<ObraMaodeobra, BigDecimal> total = new TableColumn<>("Total");
        total.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCustoTotal()));
        configurarMoeda(custo);
        configurarMoeda(total);
        tabela.getColumns().addAll(func, coluna("Horas", "horas", 80), custo, total);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        obraInfoMaoDeObra = subtitulo("Selecione uma obra no separador Obras primeiro.");
        VBox form = painelLateral("Alocar Funcionario");
        ComboBox<Funcionario> cbFunc = comboFuncionarios();
        TextField horas = campo("Horas *"), custoTf = campo("Custo/hora *");
        Label status = statusLabel();
        Button guardar = botao("Alocar", "#173f6b");
        guardar.setOnAction(e -> {
            if (obraSelecionada == null || cbFunc.getValue() == null) {
                status(status, false, "Selecione obra e funcionario.");
                return;
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
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        form.getChildren().addAll(label("Funcionario"), cbFunc, label("Horas"), horas, label("Custo/hora"), custoTf, guardar, status);
        BorderPane painel = painelPrincipal(tabela, form, botaoAtualizar(this::carregarMaodeobra));
        painel.setTop(new VBox(6, obraInfoMaoDeObra, new HBox(10, botaoAtualizar(this::carregarMaodeobra))));
        return painel;
    }

    private BorderPane criarPainelAutomedicoes() {
        TableView<Automedicao> tabela = new TableView<>(automedicoes);
        TableColumn<Automedicao, String> obra = new TableColumn<>("Obra");
        obra.setCellValueFactory(c -> new SimpleStringProperty(idObra(c.getValue().getObraid())));
        TableColumn<Automedicao, BigDecimal> valor = coluna("Valor", "valor", 120);
        configurarMoeda(valor);
        tabela.getColumns().addAll(coluna("ID", "id", 60), obra, coluna("% Trabalho", "percentagemtrabalho", 120), coluna("Estado", "estado", 100), valor);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox form = painelLateral("Nova Auto-medicao");
        ComboBox<Obra> cbObra = comboObras();
        TextField tfPercentagem = campo("Percentagem *");
        TextField tfValor = campo("Valor");
        Label status = statusLabel();
        Button guardar = botao("Criar Auto-medicao", "#ef6b2e");
        guardar.setOnAction(e -> {
            try {
                Automedicao a = new Automedicao();
                a.setObraid(cbObra.getValue());
                a.setPercentagemtrabalho(new BigDecimal(tfPercentagem.getText().trim()));
                a.setValor(parse(tfValor.getText()));
                automedicaoService.criarAutomedicao(a);
                carregarAutomedicoes();
                status(status, true, "Auto-medicao criada.");
                limpar(tfPercentagem, tfValor);
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        Button aprovar = botao("Aprovar Selecionada", "#173f6b");
        aprovar.setOnAction(e -> {
            Automedicao selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                status(status, false, "Selecione uma auto-medicao.");
                return;
            }
            automedicaoService.aprovarAutomedicao(selecionada.getId());
            carregarAutomedicoes();
            status(status, true, "Auto-medicao aprovada.");
        });
        form.getChildren().addAll(label("Obra"), cbObra, label("Percentagem"), tfPercentagem, label("Valor"), tfValor, guardar, aprovar, status);
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarAutomedicoes));
    }

    private BorderPane criarPainelFaturas() {
        TableView<Fatura> tabela = new TableView<>(faturas);
        TableColumn<Fatura, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> new SimpleStringProperty(nomeCliente(c.getValue().getClienteid())));
        TableColumn<Fatura, String> obra = new TableColumn<>("Obra");
        obra.setCellValueFactory(c -> new SimpleStringProperty(idObra(c.getValue().getObraid())));
        TableColumn<Fatura, BigDecimal> valor = coluna("Valor", "valor", 120);
        configurarMoeda(valor);
        tabela.getColumns().addAll(coluna("ID", "id", 60), coluna("Numero", "numerofatura", 140), cliente, obra, coluna("Estado", "estado", 100), valor, coluna("Emissao", "dataemissao", 110), coluna("Vencimento", "datavencimento", 110));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox form = painelLateral("Emitir Fatura");
        ComboBox<Automedicao> cbAutomedicao = comboAutomedicoes();
        TextField tfNumero = campo("Numero *");
        TextField tfValor = campo("Valor *");
        TextField tfIva = campo("IVA");
        TextField tfRetencao = campo("Retencao");
        DatePicker emissao = new DatePicker(LocalDate.now());
        DatePicker vencimento = new DatePicker(LocalDate.now().plusDays(30));
        estilizarDatePicker(emissao);
        estilizarDatePicker(vencimento);
        Label status = statusLabel();
        Button emitir = botao("Emitir Fatura", "#ef6b2e");
        emitir.setOnAction(e -> {
            Automedicao auto = cbAutomedicao.getValue();
            if (auto == null) {
                status(status, false, "Selecione uma auto-medicao.");
                return;
            }
            try {
                Fatura f = new Fatura();
                f.setAutomedicaoid(auto);
                f.setObraid(auto.getObraid());
                f.setClienteid(auto.getObraid().getClienteid());
                f.setNumerofatura(tfNumero.getText().trim());
                f.setValor(new BigDecimal(tfValor.getText().trim()));
                f.setDataemissao(emissao.getValue());
                f.setDatavencimento(vencimento.getValue());
                f.setIva(parseDefault(tfIva.getText(), new BigDecimal("23.00")));
                f.setRetencao(parseDefault(tfRetencao.getText(), BigDecimal.ZERO));
                faturaService.emitirFatura(f);
                carregarFaturas();
                status(status, true, "Fatura emitida.");
                limpar(tfNumero, tfValor, tfIva, tfRetencao);
            } catch (Exception ex) {
                status(status, false, ex.getMessage());
            }
        });
        Button pagar = botao("Marcar como Paga", "#173f6b");
        pagar.setOnAction(e -> {
            Fatura selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                status(status, false, "Selecione uma fatura.");
                return;
            }
            faturaService.registarPagamento(selecionada.getId());
            carregarFaturas();
            status(status, true, "Fatura marcada como paga.");
        });
        form.getChildren().addAll(label("Auto-medicao"), cbAutomedicao, label("Numero"), tfNumero, label("Valor"), tfValor, label("IVA"), tfIva, label("Retencao"), tfRetencao, label("Data emissao"), emissao, label("Data vencimento"), vencimento, emitir, pagar, status);
        return painelPrincipal(tabela, form, botaoAtualizar(this::carregarFaturas));
    }

    private VBox criarPainelCustos() {
        VBox painel = new VBox(20);
        painel.setPadding(new Insets(24));
        painel.getStyleClass().add("painel-principal");
        Label titulo = titulo("Resumo de Custos por Obra", 18);
        Label instrucao = subtitulo("Selecione uma obra no separador Obras e clique em Calcular.");
        Button calcular = botao("Calcular custos da obra selecionada", "#173f6b");
        calcular.setOnAction(e -> calcularCustos(painel));
        painel.getChildren().addAll(titulo, instrucao, calcular);
        return painel;
    }

    private void calcularCustos(VBox painel) {
        if (painel.getChildren().size() > 3) painel.getChildren().remove(3, painel.getChildren().size());
        if (obraSelecionada == null) {
            painel.getChildren().add(subtitulo("Nenhuma obra selecionada."));
            return;
        }
        BigDecimal mat = materialService.custoTotalMateriais(obraSelecionada.getId());
        BigDecimal mdo = maodeobraService.custoTotalMaodeobra(obraSelecionada.getId());
        BigDecimal total = mat.add(mdo);
        BigDecimal orc = obraSelecionada.getPropostaid() == null ? BigDecimal.ZERO : obraSelecionada.getPropostaid().getValortotal();
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.add(card("Orcamento", orc, "#173f6b"), 0, 0);
        grid.add(card("Materiais", mat, "#ef6b2e"), 1, 0);
        grid.add(card("Mao de obra", mdo, "#4b6480"), 2, 0);
        grid.add(card("Total real", total, "#112b46"), 0, 1);
        painel.getChildren().addAll(subtitulo("Obra " + idObra(obraSelecionada) + " | " + obraSelecionada.getEstado()), grid);
    }

    private VBox card(String label, BigDecimal valor, String cor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setPrefWidth(230);
        card.getStyleClass().add("custo-card");
        card.setStyle("-fx-border-color: " + cor + ";");
        Label l1 = subtitulo(label);
        Label l2 = titulo(String.format("%.2f EUR", valor), 20);
        l2.setTextFill(Color.web(cor));
        card.getChildren().addAll(l1, l2);
        return card;
    }

    private void carregarDados() {
        carregarClientes();
        carregarFuncionarios();
        carregarPropostas();
        carregarObras();
        carregarAutomedicoes();
        carregarFaturas();
        carregarMateriais();
        carregarMaodeobra();
        atualizarObraSelecionada();
    }

    private void carregarClientes() { clientes.setAll(clienteService.listarTodos()); }
    private void carregarFuncionarios() { funcionarios.setAll(funcionarioService.listarTodos()); }
    private void carregarPropostas() { propostas.setAll(propostaService.listarTodas()); }
    private void carregarObras() { obras.setAll(obraService.listarTodas()); }
    private void carregarAutomedicoes() { automedicoes.setAll(automedicaoService.listarTodas()); }
    private void carregarFaturas() { faturas.setAll(faturaService.listarTodas()); }
    private void carregarMateriais() { materiais.clear(); if (obraSelecionada != null) materiais.addAll(materialService.listarPorObra(obraSelecionada.getId())); }
    private void carregarMaodeobra() { maodeobra.clear(); if (obraSelecionada != null) maodeobra.addAll(maodeobraService.listarPorObra(obraSelecionada.getId())); }

    private void atualizarObraSelecionada() {
        String texto = obraSelecionada == null
            ? "Selecione uma obra no separador Obras primeiro."
            : "Obra selecionada: " + idObra(obraSelecionada) + " | " + obraSelecionada.getEstado() + " | " + obraSelecionada.getPercentagemconclusao() + "%";
        if (obraInfoMateriais != null) obraInfoMateriais.setText(texto);
        if (obraInfoMaoDeObra != null) obraInfoMaoDeObra.setText(texto);
    }

    private BorderPane painelPrincipal(TableView<?> tabela, VBox form, Button refresh) {
        BorderPane painel = new BorderPane();
        painel.setPadding(new Insets(20));
        painel.getStyleClass().add("painel-principal");
        tabela.setStyle("-fx-accent: " + COLOR_PRIMARY + "; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        painel.setTop(new HBox(10, refresh));
        painel.setCenter(tabela);
        painel.setRight(form);
        return painel;
    }

    private VBox painelLateral(String titulo) {
        VBox box = new VBox(10);
        box.setPrefWidth(340);
        box.setPadding(new Insets(16));
        box.getStyleClass().add("painel-lateral");
        box.getChildren().addAll(titulo(titulo, 14), new Separator());
        return box;
    }

    private <T, V> TableColumn<T, V> coluna(String nome, String prop, int largura) {
        TableColumn<T, V> c = new TableColumn<>(nome);
        c.setPrefWidth(largura);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private <T> void configurarMoeda(TableColumn<T, BigDecimal> coluna) {
        coluna.setCellFactory(col -> new TableCell<T, BigDecimal>() {
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f EUR", item));
            }
        });
    }

    private Label titulo(String texto, int tamanho) {
        Label l = new Label(texto);
        l.getStyleClass().add("label-titulo");
        if (tamanho != 22) l.setStyle("-fx-font-size: " + tamanho + ";");
        return l;
    }

    private Label subtitulo(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("label-subtitulo");
        return l;
    }

    private Label label(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("label-normal");
        return l;
    }

    private Label statusLabel() {
        Label l = new Label();
        l.setWrapText(true);
        l.setFont(Font.font("Segoe UI", 12));
        return l;
    }

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        // CSS handles .text-field styles
        return tf;
    }

    private TextArea area(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(4);
        ta.setWrapText(true);
        // CSS handles .text-area styles
        return ta;
    }

    private Button botao(String texto, String cor) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 10 18; -fx-background-radius: 10;");
        return b;
    }

    private Button botaoAtualizar(Runnable fn) {
        Button b = botao("Atualizar", "#173f6b");
        b.setOnAction(e -> fn.run());
        return b;
    }

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
            @Override protected void updateItem(Cliente i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "" : "#" + i.getId() + " - " + i.getNome()); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Cliente i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "Selecionar cliente..." : i.getNome()); }
        });
        return cb;
    }

    private ComboBox<Proposta> comboPropostas() {
        ComboBox<Proposta> cb = new ComboBox<>(propostas);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Proposta i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "" : "#" + i.getId() + " - " + nomeCliente(i.getClienteid())); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Proposta i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "Selecionar proposta..." : "#" + i.getId()); }
        });
        return cb;
    }

    private ComboBox<Obra> comboObras() {
        ComboBox<Obra> cb = new ComboBox<>(obras);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Obra i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "" : idObra(i) + " - " + nomeCliente(i.getClienteid())); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Obra i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "Selecionar obra..." : idObra(i)); }
        });
        return cb;
    }

    private ComboBox<Funcionario> comboFuncionarios() {
        ComboBox<Funcionario> cb = new ComboBox<>(funcionarios);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Funcionario i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "" : i.getNome() + " (" + i.getCargo() + ")"); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Funcionario i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "Selecionar funcionario..." : i.getNome()); }
        });
        return cb;
    }

    private ComboBox<Automedicao> comboAutomedicoes() {
        ComboBox<Automedicao> cb = new ComboBox<>(automedicoes);
        cb.setMaxWidth(Double.MAX_VALUE);
        estilizarCombo(cb);
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Automedicao i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "" : "#" + i.getId() + " - " + idObra(i.getObraid())); }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Automedicao i, boolean empty) { super.updateItem(i, empty); setText(empty || i == null ? "Selecionar auto-medicao..." : "#" + i.getId()); }
        });
        return cb;
    }

    private String nomeCliente(Cliente c) {
        Integer id = c == null ? null : c.getId();
        if (id == null) return "-";
        for (Cliente cliente : clientes) {
            if (id.equals(cliente.getId())) return cliente.getNome();
        }
        return "#" + id;
    }

    private String nomeFuncionario(Funcionario f) {
        Integer id = f == null ? null : f.getId();
        if (id == null) return "-";
        for (Funcionario funcionario : funcionarios) {
            if (id.equals(funcionario.getId())) return funcionario.getNome();
        }
        return "#" + id;
    }

    private void estilizarCombo(ComboBox<?> cb) {
        // CSS handles .combo-box styles
    }

    private void estilizarDatePicker(DatePicker datePicker) {
        // CSS handles .date-picker styles
    }

    private String idObra(Obra obra) { return obra == null || obra.getId() == null ? "-" : "#" + obra.getId(); }
    private BigDecimal parse(String valor) { return valor == null || valor.isBlank() ? BigDecimal.ZERO : new BigDecimal(valor.trim()); }
    private BigDecimal parseDefault(String valor, BigDecimal fallback) { return valor == null || valor.isBlank() ? fallback : new BigDecimal(valor.trim()); }
    private void limpar(TextField... campos) { for (TextField campo : campos) campo.clear(); }
    private void status(Label lbl, boolean ok, String txt) { lbl.setTextFill(Color.web(ok ? "#86efac" : "#f87171")); lbl.setText(txt == null || txt.isBlank() ? "Operacao invalida." : txt); }
}
