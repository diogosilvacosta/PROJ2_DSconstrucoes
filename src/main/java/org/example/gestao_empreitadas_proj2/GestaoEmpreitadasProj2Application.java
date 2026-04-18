package org.example.gestao_empreitadas_proj2;

import org.example.gestao_empreitadas_proj2.models.*;
import org.example.gestao_empreitadas_proj2.services.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class GestaoEmpreitadasProj2Application {

    public static void main(String[] args) {
        SpringApplication.run(GestaoEmpreitadasProj2Application.class, args);
    }

    @Value("${app.run-tests:false}")
    private boolean runTests;

    @Bean
    CommandLineRunner testarTudo(
            ClienteService clienteService,
            FuncionarioService funcionarioService,
            PropostaService propostaService,
            ObraService obraService,
            AutomedicaoService automedicaoService,
            FaturaService faturaService,
            TrabalhodiarioService trabalhodiarioService,
            PlanotrabalhoService planotrabalhoService,
            ObraMaterialService obraMaterialService,
            ObraMaodeobraService obraMaodeobraService,
            ItemmedicaoService itemmedicaoService) {

        return args -> {
            if (!runTests) {
                System.out.println("\nServidor web: http://localhost:8080");
                System.out.println("App desktop: executar a classe DesktopLauncher para abrir a interface JavaFX.\n");
                return;
            }
            long ts = System.currentTimeMillis();

            System.out.println("\n========================================");
            System.out.println("   GESTÃO DE EMPREITADAS - TESTES BLL   ");
            System.out.println("========================================\n");

            // ============================================================
            // 1. CLIENTES
            // ============================================================
            System.out.println("--- [1] CLIENTES ---");

            // Caso válido
            String nifBase = String.valueOf(100000000L + (ts % 800000000L));
            Cliente cliente = new Cliente();
            cliente.setNome("Construtora Silva & Filhos");
            cliente.setNif(nifBase);
            cliente.setEmail("silva_construtora_" + ts + "@email.com");
            cliente.setMorada("Rua das Obras, 10, Porto");
            cliente.setTelefone("912345678");
            clienteService.registarCliente(cliente);

            // Caso inválido: NIF errado
            Cliente clienteInvalido = new Cliente();
            clienteInvalido.setNome("Empresa Inválida");
            clienteInvalido.setNif("123"); // NIF inválido
            clienteInvalido.setEmail("invalido_" + ts + "@email.com");
            clienteService.registarCliente(clienteInvalido);

            // ============================================================
            // 2. FUNCIONÁRIOS
            // ============================================================
            System.out.println("\n--- [2] FUNCIONÁRIOS ---");

            Funcionario funcionario = new Funcionario();
            funcionario.setNome("Eng. Angelo Pereira");
            funcionario.setCargo("Engenheiro eletrotecnico");
            funcionario.setEmail("angelo_pereira_" + ts + "@empresa.com");
            funcionarioService.registarFuncionario(funcionario);

            // ============================================================
            // 3. PROPOSTAS + ITENS DE MEDIÇÃO
            // ============================================================
            System.out.println("\n--- [3] PROPOSTAS ---");

            Proposta proposta = new Proposta();
            proposta.setClienteid(cliente);
            proposta.setValortotal(new BigDecimal("85000.00"));
            proposta.setDataproposta(LocalDate.now());
            proposta.setEstado("RASCUNHO");
            propostaService.criarProposta(proposta);

            System.out.println("\n--- [3.1] ITENS DE MEDIÇÃO ---");

            Itemmedicao item1 = new Itemmedicao();
            item1.setPropostaid(proposta);
            item1.setDescricao("Betonagem de fundações");
            item1.setQuantidade(new BigDecimal("50.00"));
            item1.setValorunitario(new BigDecimal("120.00"));
            itemmedicaoService.adicionarItem(item1);

            Itemmedicao item2 = new Itemmedicao();
            item2.setPropostaid(proposta);
            item2.setDescricao("Alvenaria exterior");
            item2.setQuantidade(new BigDecimal("200.00"));
            item2.setValorunitario(new BigDecimal("85.00"));
            itemmedicaoService.adicionarItem(item2);

            // Item inválido: valor unitário negativo
            Itemmedicao itemInvalido = new Itemmedicao();
            itemInvalido.setPropostaid(proposta);
            itemInvalido.setDescricao("Item inválido");
            itemInvalido.setQuantidade(new BigDecimal("10.00"));
            itemInvalido.setValorunitario(new BigDecimal("-5.00"));
            itemmedicaoService.adicionarItem(itemInvalido);

            // ============================================================
            // 4. OBRAS
            // ============================================================
            System.out.println("\n--- [4] OBRAS ---");

            Obra obra = new Obra();
            obra.setClienteid(cliente);
            obra.setPropostaid(proposta);
            obra.setDirectorobra(funcionario);
            obra.setDatainicio(LocalDate.now());
            obra.setEstado("INICIADA");
            obra.setPercentagemconclusao(BigDecimal.ZERO);
            obraService.criarObra(obra);

            // ============================================================
            // 5. PLANO DE TRABALHOS
            // ============================================================
            System.out.println("\n--- [5] PLANO DE TRABALHOS ---");

            Planotrabalho fase1 = new Planotrabalho();
            fase1.setObraid(obra);
            fase1.setDescricao("Fase 1 - Fundações");
            fase1.setDuracao(30);
            fase1.setEstado("PLANEJADA");
            planotrabalhoService.criarFase(fase1);

            Planotrabalho fase2 = new Planotrabalho();
            fase2.setObraid(obra);
            fase2.setDescricao("Fase 2 - Estrutura");
            fase2.setDuracao(60);
            fase2.setEstado("PLANEJADA");
            planotrabalhoService.criarFase(fase2);

            // Iniciar fase 1
            planotrabalhoService.iniciarFase(fase1.getId());

            // ============================================================
            // 6. MATERIAIS DA OBRA
            // ============================================================
            System.out.println("\n--- [6] MATERIAIS DA OBRA ---");

            ObraMaterial material1 = new ObraMaterial();
            material1.setObraid(obra);
            material1.setDescricao("Cimento Portland 42.5");
            material1.setUnidade("saco");
            material1.setQuantidade(new BigDecimal("50.00"));
            material1.setPrecoUnitario(new BigDecimal("8.50"));
            obraMaterialService.adicionarMaterial(material1);

            ObraMaterial material2 = new ObraMaterial();
            material2.setObraid(obra);
            material2.setDescricao("Ferro em varão Ø12");
            material2.setUnidade("kg");
            material2.setQuantidade(new BigDecimal("200.00"));
            material2.setPrecoUnitario(new BigDecimal("1.20"));
            obraMaterialService.adicionarMaterial(material2);

            // Caso inválido: quantidade negativa
            ObraMaterial materialInvalido = new ObraMaterial();
            materialInvalido.setObraid(obra);
            materialInvalido.setDescricao("Material inválido");
            materialInvalido.setQuantidade(new BigDecimal("-5.00"));
            materialInvalido.setPrecoUnitario(new BigDecimal("10.00"));
            obraMaterialService.adicionarMaterial(materialInvalido);

            System.out.printf("[BLL] Custo total materiais obra: %.2f€%n",
                obraMaterialService.custoTotalMateriais(obra.getId()));

            // ============================================================
            // 7. MÃO DE OBRA DA OBRA
            // ============================================================
            System.out.println("\n--- [7] MÃO DE OBRA ---");

            ObraMaodeobra mdo1 = new ObraMaodeobra();
            mdo1.setObraid(obra);
            mdo1.setFuncionarioid(funcionario);
            mdo1.setHoras(80);
            mdo1.setCustoHora(new BigDecimal("18.00"));
            obraMaodeobraService.alocarMaodeobra(mdo1);

            // Caso inválido: duplicar o mesmo funcionário na mesma obra
            ObraMaodeobra mdoDuplicado = new ObraMaodeobra();
            mdoDuplicado.setObraid(obra);
            mdoDuplicado.setFuncionarioid(funcionario);
            mdoDuplicado.setHoras(40);
            mdoDuplicado.setCustoHora(new BigDecimal("18.00"));
            obraMaodeobraService.alocarMaodeobra(mdoDuplicado);

            System.out.printf("[BLL] Custo total mão de obra obra: %.2f€%n",
                obraMaodeobraService.custoTotalMaodeobra(obra.getId()));

            // ============================================================
            // 8. TRABALHO DIÁRIO
            // ============================================================
            System.out.println("\n--- [8] TRABALHO DIÁRIO ---");

            Trabalhodiario trabalho = new Trabalhodiario();
            trabalho.setObraid(obra);
            trabalho.setData(LocalDate.now());
            trabalho.setHorastrabalhadas(8);
            trabalho.setPercentagemrealizado(new BigDecimal("15.00"));
            trabalho.setDescricao("Escavação e preparação do terreno");
            trabalhodiarioService.registarTrabalho(trabalho);

            // Caso inválido: mais de 24 horas
            Trabalhodiario trabalhoInvalido = new Trabalhodiario();
            trabalhoInvalido.setObraid(obra);
            trabalhoInvalido.setData(LocalDate.now());
            trabalhoInvalido.setHorastrabalhadas(25);
            trabalhoInvalido.setPercentagemrealizado(new BigDecimal("5.00"));
            trabalhodiarioService.registarTrabalho(trabalhoInvalido);

            // ============================================================
            // 8. AUTO-MEDIÇÃO
            // ============================================================
            System.out.println("\n--- [9] AUTO-MEDIÇÃO ---");

            Automedicao automedicao = new Automedicao();
            automedicao.setObraid(obra);
            automedicao.setPercentagemtrabalho(new BigDecimal("15.00"));
            automedicao.setEstado("RASCUNHO");
            automedicao.setValor(new BigDecimal("12750.00"));
            automedicaoService.criarAutomedicao(automedicao);

            // Aprovar a auto-medição
            automedicaoService.aprovarAutomedicao(automedicao.getId());

            // ============================================================
            // 9. FATURAS
            // ============================================================
            System.out.println("\n--- [10] FATURAS ---");

            Fatura fatura = new Fatura();
            fatura.setAutomedicaoid(automedicao);
            fatura.setObraid(obra);
            fatura.setClienteid(cliente);
            fatura.setNumerofatura("FAT-" + ts);
            fatura.setValor(new BigDecimal("12750.00"));
            fatura.setDataemissao(LocalDate.now());
            fatura.setDatavencimento(LocalDate.now().plusDays(30));
            fatura.setIva(new BigDecimal("23.00"));
            fatura.setRetencao(BigDecimal.ZERO);
            faturaService.emitirFatura(fatura);

            // Registar pagamento
            faturaService.registarPagamento(fatura.getId());

            // ============================================================
            // RESUMO FINAL
            // ============================================================
            System.out.println("\n========================================");
            System.out.println("   TESTES CONCLUÍDOS COM SUCESSO!        ");
            System.out.println("   Clientes:      " + clienteService.listarTodos().size());
            System.out.println("   Propostas:     " + propostaService.listarTodas().size());
            System.out.println("   Obras:         " + obraService.listarTodas().size());
            System.out.println("   Auto-medições: " + automedicaoService.listarTodas().size());
            System.out.println("   Faturas:       " + faturaService.listarTodas().size());
            System.out.println("========================================\n");
        };
    }
}
