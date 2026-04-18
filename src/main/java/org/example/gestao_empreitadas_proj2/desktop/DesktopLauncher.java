package org.example.gestao_empreitadas_proj2.desktop;

import org.example.gestao_empreitadas_proj2.GestaoEmpreitadasProj2Application;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(GestaoEmpreitadasProj2Application.class)
            .headless(false)
            .web(WebApplicationType.NONE)
            .run(args);

        DesktopApp.setContext(context);
        DesktopApp.launch(DesktopApp.class, args);
    }
}
