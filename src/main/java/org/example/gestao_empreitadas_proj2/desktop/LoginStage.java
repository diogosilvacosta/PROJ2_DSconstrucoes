package org.example.gestao_empreitadas_proj2.desktop;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.gestao_empreitadas_proj2.models.Utilizador;
import org.example.gestao_empreitadas_proj2.services.UtilizadorService;

import java.util.Optional;
import java.util.function.Consumer;

public class LoginStage {

    private final UtilizadorService utilizadorService;
    private final Consumer<Utilizador> onLoginSucesso;
    private Stage stage;

    public LoginStage(UtilizadorService utilizadorService, Consumer<Utilizador> onLoginSucesso) {
        this.utilizadorService = utilizadorService;
        this.onLoginSucesso = onLoginSucesso;
    }

    public void mostrar() {
        stage = new Stage();
        stage.setTitle("DS Construções — Entrar");
        stage.setResizable(false);

        // Fundo principal
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #0d1f33;");

        // Cabeçalho com logo
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(40, 40, 24, 40));
        header.setStyle("-fx-background-color: #0d1f33;");

        ImageView logo = carregarLogo();
        if (logo != null) header.getChildren().add(logo);

        Label titulo = new Label("DS Construções");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titulo.setTextFill(Color.WHITE);

        Label subtitulo = new Label("Gestão Interna de Empreitadas");
        subtitulo.setFont(Font.font("Segoe UI", 13));
        subtitulo.setTextFill(Color.web("#9db0c5"));

        header.getChildren().addAll(titulo, subtitulo);

        // Painel de login
        VBox painelLogin = new VBox(14);
        painelLogin.setAlignment(Pos.CENTER);
        painelLogin.setPadding(new Insets(32, 40, 36, 40));
        painelLogin.setStyle(
            "-fx-background-color: #152233;" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: #22344a;" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;"
        );
        painelLogin.setMaxWidth(380);

        Label labelForm = new Label("Iniciar Sessão");
        labelForm.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        labelForm.setTextFill(Color.WHITE);

        // Username
        Label lblUser = new Label("Utilizador");
        lblUser.setFont(Font.font("Segoe UI", 12));
        lblUser.setTextFill(Color.web("#9db0c5"));
        TextField tfUser = new TextField();
        tfUser.setPromptText("username");
        estilizarCampo(tfUser);

        // Password
        Label lblPass = new Label("Palavra-passe");
        lblPass.setFont(Font.font("Segoe UI", 12));
        lblPass.setTextFill(Color.web("#9db0c5"));
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("••••••••");
        estilizarCampo(pfPass);

        // Mensagem de erro
        Label lblErro = new Label();
        lblErro.setFont(Font.font("Segoe UI", 12));
        lblErro.setTextFill(Color.web("#f87171"));
        lblErro.setWrapText(true);

        // Botão entrar
        Button btnEntrar = new Button("Entrar");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);
        btnEntrar.setStyle(
            "-fx-background-color: #ef6b2e;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: 700;" +
            "-fx-padding: 12 24;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );

        // Nota sobre credenciais padrão
        Label lblDica = new Label("Credenciais padrão: admin / admin123");
        lblDica.setFont(Font.font("Segoe UI", 11));
        lblDica.setTextFill(Color.web("#4b6480"));

        Runnable tentarLogin = () -> {
            String user = tfUser.getText().trim();
            String pass = pfPass.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                lblErro.setText("Preencha o utilizador e a palavra-passe.");
                return;
            }
            Optional<Utilizador> resultado = utilizadorService.autenticar(user, pass);
            if (resultado.isPresent()) {
                stage.close();
                onLoginSucesso.accept(resultado.get());
            } else {
                lblErro.setText("Utilizador ou palavra-passe incorretos.");
                pfPass.clear();
                pfPass.requestFocus();
            }
        };

        btnEntrar.setOnAction(e -> tentarLogin.run());
        pfPass.setOnAction(e -> tentarLogin.run());
        tfUser.setOnAction(e -> pfPass.requestFocus());

        painelLogin.getChildren().addAll(
            labelForm,
            lblUser, tfUser,
            lblPass, pfPass,
            lblErro,
            btnEntrar,
            lblDica
        );

        // Juntar tudo
        VBox centro = new VBox(20);
        centro.setAlignment(Pos.CENTER);
        centro.setPadding(new Insets(0, 40, 40, 40));
        centro.getChildren().add(painelLogin);

        root.getChildren().addAll(header, centro);

        Scene scene = new Scene(root, 460, 560);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        tfUser.requestFocus();
    }

    private void estilizarCampo(TextField tf) {
        tf.setStyle(
            "-fx-background-color: #0d1f33;" +
            "-fx-text-fill: #edf3fa;" +
            "-fx-prompt-text-fill: #4b6480;" +
            "-fx-border-color: #22344a;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 14;" +
            "-fx-font-size: 13;"
        );
    }

    private ImageView carregarLogo() {
        try {
            var resource = LoginStage.class.getResource("/static/assets/ds-construcoes-logo.png");
            if (resource == null) return null;
            Image img = new Image(resource.toExternalForm());
            ImageView iv = new ImageView(img);
            iv.setFitHeight(70);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            return iv;
        } catch (Exception e) {
            return null;
        }
    }
}
