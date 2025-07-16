# 🎮 Duelo de Personagens

**Duelo de Personagens** é um jogo de estratégia tática em Java, baseado em turnos, com tabuleiro 10x10, interface JavaFX, suporte a IA e arquitetura modularizada. O projeto permite batalhas entre dois jogadores ou contra um bot com inteligência artificial parametrizável.



## 🧱 Tecnologias e Stack

| Tecnologia     | Versão     | Finalidade                           |
|----------------|------------|--------------------------------------|
| Java           | 21         | Linguagem base                       |
| JavaFX         | 21         | UI via FXML e controles visuais |
| FXGL           | 17.3       | Motor de jogo usado na engine base   |
| Maven          | 3.8.5      | Gerenciador de dependências e build  |
| JUnit Jupiter  | 5.10.2     | Testes unitários                     |


## 📐 Arquitetura

O projeto é modular, orientado a componentes, baseado nos princípios de separação de responsabilidades:

- **MVC (Model-View-Controller)** para controle das telas.
- **Componentes de jogo**:
  - `Player` (abstração): `HumanPlayer`, `BotPlayer`
  - `Personagem` (abstração): `Guerreiro`, `Arqueiro`, `Mago`
  - `TurnManager`: fluxo e alternância de turnos
  - `GridManager`: lógica do tabuleiro e movimentação
- **FXML** para definição declarativa da interface.
- **IA Modularizada** com heurísticas específicas por nível.

## 🧠 Inteligência Artificial (Bot)

A IA é ajustável via o combo de dificuldade:

| Dificuldade | Estratégia                                                  |
|-------------|-------------------------------------------------------------|
| Fácil       | Ataca se puder, senão se aproxima                           |
| Normal      | Usa poderes sob condições específicas e prioriza alvos      |
| Difícil     | Aplica heurísticas táticas baseadas em *Minimax de 1 nível* |

> A IA considera posição, defesa, vida e simula reações do oponente para escolher a melhor ação.

## Execução

### Requisitos

- Java 21 (ou superior)
- Git (para clonar)
- Maven (opcional — wrapper incluso)

### Passos

```bash
# Clone o repositório
git clone https://github.com/torresds/duelo-personagens.git
cd duelo-personagens

# Executa com Maven Wrapper (Linux/macOS)
./mvnw clean javafx:run

# Ou no Windows
mvnw.cmd clean javafx:run
````

> O plugin JavaFX já está configurado no `pom.xml`. O ponto de entrada é `ufjf.trabalho01.GameApp`.

---

## 🗂 Estrutura do projeto

```
📦 trabalho01
├── 📁 src
│   └── 📁 main
│       ├── 📁 java
│       │   ├── module-info.java
│       │   └── ufjf.trabalho01
│       │       ├── GameApp.java
│       │       ├── GridManager.java
│       │       ├── MainUIController.java
│       │       ├── SelectionController.java
│       │       ├── 📁 jogo
│       │       │   ├── Player.java
│       │       │   ├── HumanPlayer.java
│       │       │   ├── BotPlayer.java
│       │       │   └── TurnManager.java
│       │       └── 📁 personagens
│       │           ├── Personagem.java
│       │           ├── Guerreiro.java
│       │           ├── Arqueiro.java
│       │           └── Mago.java
│       └── 📁 resources
│           └── ufjf/trabalho01
│               ├── SelectionScreen.fxml
│               ├── MainUI.fxml
│               └── 📁 sprites
│                   ├── guerreiro.png
│                   ├── arqueiro.png
│                   └── mago.png
├── pom.xml
└── mvnw / mvnw.cmd
```


## ⚔️ Personagens

| Classe    | Ataque | Defesa | Alcance | Poder Especial                       |
| --------- | ------ | ------ | ------- | ------------------------------------ |
| Guerreiro | 15     | 10     | 1       | Carga Brutal: ataque de 30           |
| Arqueiro  | 8      | 5      | 5       | Flecha Precisa: alcance +1           |
| Mago      | 10     | 7      | 3       | Trocar Vida: inverte PV com oponente |

## 🎨 Interface

* **Tela de Seleção**: modo PvP/PvC, nome, classe e dificuldade
* **Tela de Batalha**: tabuleiro 10x10 com HUD lateral (PV, Ataque, Defesa, Alcance)
* **Feedback visual**: célula destacada, mensagens de log, transições de movimento

## 👥 Créditos
Desenvolvido por:
- [**Ana Laredo**](https://github.com/analaredo) – código, design dos sprites 
- [**Miguel Torres**](https://github.com/torresds) – código

Projeto desenvolvido durante a disciplina **Orientação a Objetos**, na **UFJF**.
