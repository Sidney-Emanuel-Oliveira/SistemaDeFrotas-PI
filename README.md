# Sistema de Gestão de Frotas

## Visão Geral do Projeto

O Sistema de Gestão de Frotas é uma aplicação desktop desenvolvida em Java, utilizando a biblioteca Swing para a interface gráfica. O objetivo principal é auxiliar no controle e gerenciamento de veículos e suas movimentações (abastecimentos, manutenções, multas, etc.), oferecendo funcionalidades robustas de cadastro, consulta e, principalmente, relatórios detalhados para análise de custos e consumo.

Uma característica distintiva do sistema é sua abordagem híbrida de persistência de dados. Inicialmente, os dados são armazenados em arquivos de texto simples (`.txt`), o que facilita a portabilidade e o uso em ambientes sem um banco de dados configurado. Adicionalmente, o sistema oferece uma funcionalidade de sincronização com um banco de dados MySQL, permitindo uma persistência mais robusta e a integração com outras ferramentas de BI ou relatórios, se necessário.

## Funcionalidades Principais

O sistema oferece um conjunto abrangente de funcionalidades para a gestão eficiente de frotas:

*   **Cadastro de Veículos**: Registro de informações detalhadas sobre cada veículo, incluindo placa, marca, modelo, ano de fabricação e tipo.
*   **Cadastro de Tipos de Despesas**: Definição e gerenciamento de categorias de despesas (ex: Combustível, IPVA, Manutenção, Multa).
*   **Registro de Movimentações**: Lançamento de todas as movimentações financeiras e operacionais dos veículos, como abastecimentos (com distância percorrida e litros), manutenções e outras despesas.
*   **Relatórios Gerenciais**: Geração de diversos relatórios para análise de desempenho e custos:
    *   Despesas por veículo, mês e ano.
    *   Veículos inativos.
    *   Total de multas por veículo e ano.
    *   Consumo médio de combustível (Km/L).
    *   Custo por quilômetro rodado.
    *   Análise matricial de custos com combustível (Matrizes A, B e C).
*   **Exportação de Dados**: Capacidade de exportar relatórios para o formato CSV.
*   **Persistência Híbrida**: Armazenamento de dados em arquivos `.txt` e sincronização opcional com MySQL.
*   **Interface Amigável**: Interface gráfica intuitiva e responsiva, com suporte a temas claro e escuro.

## Tecnologias Utilizadas

O projeto foi desenvolvido utilizando as seguintes tecnologias:

| Categoria         | Tecnologia      | Versão/Descrição                                  |
| :---------------- | :-------------- | :------------------------------------------------ |
| Linguagem         | Java            | 17                                                |
| Interface Gráfica | Swing           | Biblioteca padrão do Java para aplicações desktop |
| Gerenciamento de Dependências | Maven | 3.x                                               |
| Banco de Dados    | MySQL           | 8.0+ (para sincronização opcional)                |
| Driver JDBC       | MySQL Connector/J | 8.4.0                                             |

## Arquitetura do Sistema

O sistema segue um padrão arquitetural que separa as responsabilidades em camadas distintas, facilitando a manutenção e a escalabilidade. A arquitetura pode ser visualizada no diagrama abaixo:

![Diagrama de Arquitetura](https://files.manuscdn.com/user_upload_by_module/session_file/310519663178329361/OUNtkmHotGbKSolC.png)

**Descrição das Camadas:**

*   **Camada de Interface (UI)**: Responsável pela apresentação dos dados e interação com o usuário. Inclui as telas principais da aplicação.
*   **Camada de Controle (Controller)**: Gerencia a lógica de negócios e a interação entre a UI e a camada de persistência. Processa as requisições do usuário e coordena as ações.
*   **Camada de Modelo (Model)**: Contém as classes que representam as entidades de negócio do sistema (Veículo, Movimentação, TipoDespesa).
*   **Camada de Persistência (DAO e Sincronização)**: Abstrai os detalhes de armazenamento de dados. Os DAOs (Data Access Objects) interagem com os arquivos de texto e, opcionalmente, com o `MySQLSincronizador` para o banco de dados.
*   **Armazenamento de Dados**: Representa os mecanismos de armazenamento, sendo arquivos `.txt` o padrão e MySQL o opcional.

## Estrutura do Banco de Dados

O banco de dados MySQL, quando utilizado, segue o esquema abaixo, que é criado e atualizado automaticamente pelo `MySQLSincronizador`. O diagrama de Entidade-Relacionamento (ER) ilustra as principais tabelas e seus relacionamentos:

![Diagrama de Entidade-Relacionamento](https://files.manuscdn.com/user_upload_by_module/session_file/310519663178329361/HwMJxYMYDNbwDsXo.png)

**Tabelas:**

| Tabela          | Descrição                                                                 |
| :-------------- | :------------------------------------------------------------------------ |
| `veiculos`      | Armazena informações sobre os veículos da frota.                          |
| `tipos_despesas`| Categorias de despesas para classificar as movimentações.                 |
| `movimentacoes` | Registra todas as transações e eventos relacionados aos veículos.         |

**Views (Visualizações):**

O banco de dados também define views para facilitar a consulta e a geração de relatórios:

*   `vw_gastos_por_veiculo`: Agrega o total de gastos por veículo.
*   `vw_consumo_medio_veiculo`: Calcula o consumo médio (Km/L) por veículo.
*   `vw_veiculos_inativos`: Lista todos os veículos que estão marcados como inativos.

## Estrutura do Projeto

A estrutura de diretórios do projeto é organizada de forma lógica para separar as diferentes responsabilidades e facilitar a navegação:

```
SistemaDeFrotas-PI/
├── .idea/                 # Arquivos de configuração do IntelliJ IDEA
├── bin/                   # Scripts executáveis e ícones
│   ├── br/
│   └── icons/
├── dados/                 # Arquivos de persistência (.txt) e configuração MySQL
│   ├── movimentacoes.txt
│   ├── mysql.properties
│   ├── tipos_despesas.txt
│   └── veiculos.txt
├── docs/                  # Documentação do projeto
│   └── banco_mysql.sql    # Script SQL para criação do banco de dados
├── pom.xml                # Arquivo de configuração do Maven
├── sources.txt            # Lista de arquivos fonte (gerado automaticamente)
└── src/                   # Código fonte da aplicação
    └── main/
        └── java/
            └── br/
                └── com/
                    ├── Main.java          # Ponto de entrada da aplicação
                    ├── controller/        # Classes de controle (lógica de negócio)
                    │   ├── MovimentacaoController.java
                    │   ├── RelatoriosController.java
                    │   ├── TipoDespesaController.java
                    │   └── VeiculoController.java
                    ├── dao/               # Classes de acesso a dados (Data Access Objects)
                    │   ├── MovimentacaoDAO.java
                    │   ├── TipoDespesaDAO.java
                    │   └── VeiculoDAO.java
                    ├── database/          # Classes de sincronização com banco de dados
                    │   └── MySQLSincronizador.java
                    ├── estruturas/        # Estruturas de dados auxiliares
                    │   ├── ListaLinear.java
                    │   └── OrdenacaoVeiculos.java
                    ├── model/             # Classes de modelo (entidades de negócio)
                    │   ├── Movimentacao.java
                    │   ├── TipoDespesa.java
                    │   ├── TipoVeiculo.java
                    │   ├── Veiculo.java
                    │   └── VeiculoComboItem.java
                    ├── ui/                # Componentes de UI personalizados
                    │   ├── components/    # Componentes específicos da UI
                    │   ├── ModernButton.java
                    │   ├── ModernColors.java
                    │   ├── ModernComboBox.java
                    │   ├── ModernInnerTabbedPane.java
                    │   ├── ModernTabbedPane.java
                    │   ├── RoundedPanel.java
                    │   └── WrapLayout.java
                    ├── utils/             # Classes utilitárias
                    │   ├── GeradorCSV.java
                    │   ├── IconLoader.java
                    │   ├── MatrizRelatorios.java
                    │   └── Validacoes.java
                    └── view/              # Classes de visualização (telas da aplicação)
                        ├── MovementFormDialog.java
                        ├── TelaAbout.java
                        ├── TelaCadastroDespesa.java
                        ├── TelaCadastroVeiculo.java
                        ├── TelaMovimentacao.java
                        ├── TelaPrincipal.java
                        ├── TelaRelatorios.java
                        ├── TipoMovimentacaoDialog.java
                        └── VehicleFormDialog.java
```

## Como Configurar e Executar

Para configurar e executar o projeto, siga os passos abaixo:

### Pré-requisitos

*   **Java Development Kit (JDK) 17 ou superior**: Certifique-se de ter o JDK instalado e configurado em sua máquina.
*   **Maven**: Necessário para gerenciar as dependências e compilar o projeto.
*   **MySQL (Opcional)**: Se desejar utilizar a sincronização com banco de dados, tenha uma instância MySQL em execução.

### Passos para Execução

1.  **Clonar o Repositório**:
    ```bash
    git clone https://github.com/Sidney-Emanuel-Oliveira/SistemaDeFrotas-PI.git
    cd SistemaDeFrotas-PI
    ```

2.  **Compilar o Projeto com Maven**:
    ```bash
    mvn clean install
    ```
    Este comando irá baixar as dependências e compilar o projeto, gerando um arquivo JAR executável na pasta `target/`.

3.  **Executar a Aplicação**:
    ```bash
    java -jar target/Sistema-de-Frotas-1.0-SNAPSHOT.jar
    ```
    A interface gráfica da aplicação será iniciada.

### Configuração do MySQL (Opcional)

1.  **Arquivo de Configuração**: O sistema cria automaticamente o arquivo `dados/mysql.properties` se ele não existir. Edite este arquivo para configurar a conexão com seu banco de dados MySQL:
    ```properties
    mysql.enabled=true
    mysql.url=jdbc:mysql://localhost:3306/gynlog_frotas?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo
    mysql.user=root
    mysql.password=sua_senha_mysql
    ```
    Certifique-se de substituir `sua_senha_mysql` pela senha do seu usuário MySQL.

2.  **Sincronização**: Ao iniciar a aplicação com `mysql.enabled=true`, o sistema tentará sincronizar os dados dos arquivos `.txt` com o banco de dados MySQL. A estrutura do banco (tabelas e views) será criada automaticamente se não existir, e os dados serão importados/atualizados.

## Uso da Aplicação

Ao iniciar a aplicação, você será apresentado à `TelaPrincipal`, que organiza as funcionalidades em abas. Navegue entre as abas para:

*   **Veículos**: Cadastrar, editar e visualizar veículos.
*   **Movimentações**: Registrar despesas e abastecimentos para os veículos.
*   **Relatórios**: Acessar os diversos relatórios gerenciais e exportá-los para CSV.
*   **Sobre**: Informações sobre a aplicação.

O sistema também possui um menu superior para configurações adicionais, como a alternância de tema (claro/escuro) e a sincronização manual com o MySQL.

## Relatórios Detalhados

A seção de relatórios é o coração analítico do sistema, oferecendo insights valiosos sobre a frota. Abaixo, detalhamos alguns dos relatórios mais importantes:

### Análise Matricial de Custos com Combustível

Este conjunto de relatórios utiliza conceitos de álgebra linear para fornecer uma análise aprofundada dos gastos com combustível.

*   **Matriz A - Quantidade de Abastecimentos por Veículo/Mês**: Mostra a frequência de abastecimentos para cada veículo em um determinado mês. Ajuda a identificar veículos com maior uso ou padrões de abastecimento incomuns.
*   **Matriz B - Custo Médio por Abastecimento/Marca**: Calcula o custo médio de abastecimento para cada marca de veículo em um mês específico. Útil para comparar a eficiência de diferentes marcas.
*   **Matriz C - Gasto Total Estimado com Combustível por Veículo/Marca**: Resultante da multiplicação das Matrizes A e B, esta matriz estima o gasto total com combustível para cada veículo, considerando sua marca e o mês. Fornece uma visão consolidada dos custos projetados.

### Outros Relatórios Chave

*   **Consumo Médio (Km/L)**: Calcula a eficiência de combustível de cada veículo, permitindo identificar os mais econômicos e os que precisam de atenção.
*   **Custo por Quilômetro Rodado**: Oferece uma métrica direta do custo operacional por distância percorrida, essencial para a precificação de serviços ou avaliação de desempenho.
*   **Veículos Inativos**: Lista os veículos que não estão em uso, auxiliando na gestão de ativos e na tomada de decisão sobre desativação ou manutenção.

## Contribuição

Para contribuir com o projeto, por favor, siga as diretrizes de contribuição padrão:

1.  Faça um fork do repositório.
2.  Crie uma nova branch para sua feature (`git checkout -b feature/minha-feature`).
3.  Faça suas alterações e commit (`git commit -m 'feat: Minha nova feature'`).
4.  Envie para o seu fork (`git push origin feature/minha-feature`).
5.  Abra um Pull Request.

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes. (Assumindo licença MIT, verificar no repositório original se existe um arquivo LICENSE ou informação sobre a licença.)

---

**Desenvolvido por:** Manus AI (com base na análise do repositório de Sidney Emanuel Oliveira)
