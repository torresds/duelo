package ufjf.trabalho01.jogo;

import ufjf.trabalho01.GridManager;
import ufjf.trabalho01.personagens.Personagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import static ufjf.trabalho01.GridManager.GRID_SIZE;

public class BotPlayer extends Player {
    private Personagem oponente;
    private boolean usouPoderEspecial = false;
    private final String difficulty;

    private record ActionScore(String action, char direction, double score) {}

    public BotPlayer(String nome, Personagem oponente, String difficulty) {
        super(nome);
        this.oponente = oponente;
        this.difficulty = difficulty;
    }

    public String chooseAndExecuteAction(GridManager gm, Player opponentPlayer) {
        this.oponente = opponentPlayer.getPersonagem();

        return switch (difficulty) {
            case "Fácil" -> easyAction(gm, opponentPlayer);
            case "Normal" -> normalAction(gm, opponentPlayer);
            case "Difícil" -> hardAction(gm, opponentPlayer);
            default -> normalAction(gm, opponentPlayer);
        };
    }

    // --- NÍVEL FÁCIL ---
    private String easyAction(GridManager gm, Player opponentPlayer) {
        if (personagem.calcularDistancia(oponente) <= personagem.getAlcanceDeAtaque()) {
            return attack(opponentPlayer);
        } else {
            moveTowardsOpponent(gm);
            return getNome() + " se aproximou do oponente.";
        }
    }

    // --- NÍVEL NORMAL ---
    private String normalAction(GridManager gm, Player opponentPlayer) {
        if ("Guerreiro".equals(personagem.getTipo()) && !usouPoderEspecial && personagem.calcularDistancia(oponente) <= 1) {
            usouPoderEspecial = true;
            return personagem.usarPoderEspecial(oponente);
        }
        if ("Mago".equals(personagem.getTipo()) && personagem.getPontosDeVida() < 40 && oponente.getPontosDeVida() > 60) {
            return personagem.usarPoderEspecial(oponente);
        }
        return easyAction(gm, opponentPlayer);
    }

    // --- NÍVEL DIFÍCIL ---
    private String hardAction(GridManager gm, Player opponentPlayer) {
        List<ActionScore> possibleActions = new ArrayList<>();

        if (personagem.calcularDistancia(oponente) <= personagem.getAlcanceDeAtaque()) {
            if (oponente.getPontosDeDefesa() + oponente.getPontosDeVida() <= personagem.getForcaDeAtaque()) {
                return attack(opponentPlayer);
            }
        }

        if ("Guerreiro".equals(personagem.getTipo()) && !usouPoderEspecial && personagem.calcularDistancia(oponente) <= 1) {
            if (oponente.getPontosDeDefesa() + oponente.getPontosDeVida() <= 30) { // Ataque do poder é 30
                usouPoderEspecial = true;
                return personagem.usarPoderEspecial(oponente);
            }
        }

        int currentX = personagem.getPosicaoX();
        int currentY = personagem.getPosicaoY();

        // 1. Avaliar ATAQUE
        if (personagem.calcularDistancia(oponente) <= personagem.getAlcanceDeAtaque()) {
            double score = evaluateActionWithOpponentResponse(gm, "ATTACK", ' ');
            possibleActions.add(new ActionScore("ATTACK", ' ', score));
        }

        // 2. Avaliar DEFESA
        double defenseScore = evaluateActionWithOpponentResponse(gm, "DEFEND", ' ');
        possibleActions.add(new ActionScore("DEFEND", ' ', defenseScore));

        // 3. Avaliar PODER ESPECIAL
        if (!usouPoderEspecial || "Mago".equals(personagem.getTipo())) {
            double powerScore = evaluateActionWithOpponentResponse(gm, "POWER", ' ');
            if (powerScore > Double.MIN_VALUE + 1) {
                possibleActions.add(new ActionScore("POWER", ' ', powerScore));
            }
        }

        // 4. Avaliar MOVIMENTO
        char[] directions = {'C', 'B', 'E', 'D'};
        for (char dir : directions) {
            if (isValidMove(gm, dir)) {
                double moveScore = evaluateActionWithOpponentResponse(gm, "MOVE", dir);
                possibleActions.add(new ActionScore("MOVE", dir, moveScore));
            }
        }

        // 5. Escolher a melhor ação com base na análise de risco
        ActionScore bestAction = possibleActions.stream()
                .max(Comparator.comparingDouble(ActionScore::score))
                .orElse(new ActionScore("DEFEND", ' ', -9999)); // Ação padrão

        // 6. Executar a melhor ação
        switch (bestAction.action()) {
            case "ATTACK":
                return attack(opponentPlayer);
            case "POWER":
                if ("Guerreiro".equals(personagem.getTipo()) || "Arqueiro".equals(personagem.getTipo())) {
                    usouPoderEspecial = true;
                }
                return personagem.usarPoderEspecial(oponente);
            case "MOVE":
                int x = personagem.getPosicaoX();
                int y = personagem.getPosicaoY();
                switch (Character.toUpperCase(bestAction.direction())) {
                    case 'C' -> y--;
                    case 'B' -> y++;
                    case 'E' -> x--;
                    case 'D' -> x++;
                }
                gm.movePersonagem(personagem, x, y);
                return getNome() + " moveu-se taticamente.";
            case "DEFEND":
            default:
                personagem.restoreDefense();
                return getNome() + " está em postura defensiva!";
        }
    }

    /**
     * Simula uma ação do Bot, e para o estado resultante, simula algumas respostas do oponente, pegando o pior resultado para o Bot.
     */
    private double evaluateActionWithOpponentResponse(GridManager gm, String botAction, char direction) {
        SimulatedState stateAfterBotMove = simulateBotAction(botAction, direction);

        double worstScoreForBot = Double.MAX_VALUE;

        String[] opponentActions = {"ATTACK", "DEFEND", "POWER"};

        for(String oppAction : opponentActions){
            SimulatedState finalState = simulateOpponentAction(stateAfterBotMove, oppAction);
            double finalScore = evaluateState(finalState);
            if(finalScore < worstScoreForBot){
                worstScoreForBot = finalScore;
            }
        }
        return worstScoreForBot;
    }


    /**
     * Avalia um estado de jogo, dando mais peso a certos fatores dependendo do contexto (vida baixa, etc).
     */
    private double evaluateState(SimulatedState state) {
        // --- Pesos Dinâmicos ---
        double healthWeight = 2.0;
        if (state.myHealth < 35) {
            healthWeight = 3.0;
        }

        // Fator 1: Vantagem de Vida e Defesa (Pontos de Vida Efetivos)
        if (state.oppHealth <= 0) return Double.MAX_VALUE;
        if (state.myHealth <= 0) return Double.MIN_VALUE;

        double myEffectiveHealth = state.myHealth + (state.myDefense * 0.7);
        double oppEffectiveHealth = state.oppHealth + (state.oppDefense * 0.7);
        double healthScore = (myEffectiveHealth - oppEffectiveHealth) * healthWeight;

        // Fator 2: Posicionamento Tático (Lógica mantida, pois já é robusta)
        double positionScore = 0;
        int dist = Math.max(Math.abs(state.myX - oponente.getPosicaoX()), Math.abs(state.myY - oponente.getPosicaoY()));
        boolean iAmInRange = dist <= personagem.getAlcanceDeAtaque();
        if ("Guerreiro".equals(personagem.getTipo())) positionScore -= dist * 2;
        else if(iAmInRange) positionScore += dist; else positionScore -= dist * 3;

        return healthScore + positionScore;
    }


    // --- MÉTODOS DE SIMULAÇÃO E AUXILIARES ---

    private record SimulatedState(int myHealth, int myDefense, int oppHealth, int oppDefense, int myX, int myY) {}

    private SimulatedState simulateBotAction(String action, char direction) {
        int myHealth = personagem.getPontosDeVida();
        int myDefense = personagem.getPontosDeDefesa();
        int oppHealth = oponente.getPontosDeVida();
        int oppDefense = oponente.getPontosDeDefesa();
        int myX = personagem.getPosicaoX();
        int myY = personagem.getPosicaoY();

        switch (action) {
            case "ATTACK": {
                int damage = personagem.getForcaDeAtaque();
                int absorbed = Math.min(damage, oppDefense);
                oppDefense -= absorbed;
                oppHealth -= (damage - absorbed);
                break;
            }
            case "DEFEND": {
                myDefense = personagem.getPontosDeDefesaMax();
                break;
            }
            case "POWER": {
                switch (personagem.getTipo()) {
                    case "Guerreiro":
                        if (!usouPoderEspecial) {
                            int damage = 30;
                            int absorbed = Math.min(damage, oppDefense);
                            oppDefense -= absorbed;
                            oppHealth -= (damage - absorbed);
                        }
                        break;
                    case "Mago":
                        int tempHealth = myHealth;
                        myHealth = oppHealth;
                        oppHealth = tempHealth;
                        break;
                    case "Arqueiro":
                        break;
                }
                break;
            }
            case "MOVE": {
                switch (direction) {
                    case 'C' -> myY--;
                    case 'B' -> myY++;
                    case 'E' -> myX--;
                    case 'D' -> myX++;
                }
                break;
            }
        }

        if (myHealth < 0) myHealth = 0;
        if (oppHealth < 0) oppHealth = 0;

        return new SimulatedState(myHealth, myDefense, oppHealth, oppDefense, myX, myY);
    }

    private SimulatedState simulateOpponentAction(SimulatedState initialState, String oppAction) {
        int myHealth = initialState.myHealth();
        int myDefense = initialState.myDefense();
        int oppHealth = initialState.oppHealth();
        int oppDefense = initialState.oppDefense();
        int myX = initialState.myX();
        int myY = initialState.myY();

        int dist = Math.max(Math.abs(myX - oponente.getPosicaoX()), Math.abs(myY - oponente.getPosicaoY()));

        switch (oppAction) {
            case "ATTACK":
                if (dist <= oponente.getAlcanceDeAtaque()) {
                    int damage = oponente.getForcaDeAtaque();
                    int absorbed = Math.min(damage, myDefense);
                    myDefense -= absorbed;
                    myHealth -= (damage - absorbed);
                }
                break;

            case "DEFEND":
                oppDefense = oponente.getPontosDeDefesaMax();
                break;

            case "POWER":
                switch (oponente.getTipo()) {
                    case "Guerreiro":
                        if (dist <= 1) { // Alcance do Guerreiro é 1
                            int damage = 30;
                            int absorbed = Math.min(damage, myDefense);
                            myDefense -= absorbed;
                            myHealth -= (damage - absorbed);
                        }
                        break;
                    case "Mago":
                        int tempHealth = myHealth;
                        myHealth = oppHealth;
                        oppHealth = tempHealth;
                        break;
                    case "Arqueiro":
                        break;
                }
                break;
        }

        if (myHealth < 0) myHealth = 0;
        if (oppHealth < 0) oppHealth = 0;

        return new SimulatedState(myHealth, myDefense, oppHealth, oppDefense, myX, myY);
    }

    private boolean isValidMove(GridManager gm, char dir) {
        int x = personagem.getPosicaoX();
        int y = personagem.getPosicaoY();
        switch (dir) {
            case 'C' -> y--;
            case 'B' -> y++;
            case 'E' -> x--;
            case 'D' -> x++;
        }

        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            return false;
        }
        return !gm.isCellOccupied(x, y);
    }

    public String attack(Player opponentPlayer) {
        Personagem alvo = opponentPlayer.getPersonagem();
        int danoTotal = personagem.getForcaDeAtaque();
        alvo.sofrerDano(danoTotal);
        return getNome() + " atacou " + alvo.getNome() + " com força de " + danoTotal + ".";
    }

    private void moveTowardsOpponent(GridManager gm) {
        int x = personagem.getPosicaoX();
        int y = personagem.getPosicaoY();
        int ex = oponente.getPosicaoX();
        int ey = oponente.getPosicaoY();

        int currentX = x;
        int currentY = y;

        if (x < ex) x++;
        else if (x > ex) x--;
        else if (y < ey) y++;
        else if (y > ey) y--;

        if(!gm.movePersonagem(personagem, x, y)){
            if(currentX < ex || currentX > ex){
                if(currentY < ey) gm.movePersonagem(personagem, currentX, currentY + 1);
                else gm.movePersonagem(personagem, currentX, currentY - 1);
            } else {
                if(currentX < ex) gm.movePersonagem(personagem, currentX + 1, currentY);
                else gm.movePersonagem(personagem, currentX - 1, currentY);
            }
        }
    }
}