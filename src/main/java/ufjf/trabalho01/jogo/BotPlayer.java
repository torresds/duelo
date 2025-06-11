package ufjf.trabalho01.jogo;

import ufjf.trabalho01.GridManager;
import ufjf.trabalho01.personagens.Personagem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BotPlayer extends Player {
    private Personagem oponente;
    private boolean usouPoderEspecial = false;
    private final String difficulty;

    /**
     * Estrutura interna para armazenar um estado de jogo simulado.
     */
    private record SimulatedState(int myHealth, int myDefense, int oppHealth, int oppDefense, int myX, int myY) {}

    /**
     * Estrutura interna para associar uma ação a sua pontuação heurística.
     */
    private record ActionScore(String action, char direction, double score) {}


    /**
     * Construtor para o BotPlayer.
     * @param nome O nome do bot.
     * @param oponente O personagem oponente inicial.
     * @param difficulty A dificuldade da IA ("Fácil", "Normal", "Difícil").
     */
    public BotPlayer(String nome, Personagem oponente, String difficulty) {
        super(nome);
        this.oponente = oponente;
        this.difficulty = difficulty;
    }

    /**
     * Ponto de entrada principal para a lógica da IA. Escolhe e executa a melhor ação
     * com base na dificuldade selecionada.
     * @param gm O gerenciador do grid para interações com o tabuleiro.
     * @param opponentPlayer O jogador oponente.
     * @param onTurnEnd Ação a ser executada quando o turno do bot terminar (especialmente após animações).
     * @return Uma string de log descrevendo a ação tomada.
     */
    public String chooseAndExecuteAction(GridManager gm, Player opponentPlayer, Runnable onTurnEnd) {
        this.oponente = opponentPlayer.getPersonagem();

        return switch (difficulty) {
            case "Fácil" -> easyAction(gm, opponentPlayer, onTurnEnd);
            case "Normal" -> normalAction(gm, opponentPlayer, onTurnEnd);
            case "Difícil" -> hardAction(gm, opponentPlayer, onTurnEnd);
            default -> normalAction(gm, opponentPlayer, onTurnEnd);
        };
    }

    /**
     * Lógica para a dificuldade "Fácil". Ataca se estiver no alcance, senão se aproxima.
     * @param gm O gerenciador do grid.
     * @param opponentPlayer O jogador oponente.
     * @param onTurnEnd O callback de final de turno.
     * @return Uma string de log.
     */
    private String easyAction(GridManager gm, Player opponentPlayer, Runnable onTurnEnd) {
        if (personagem.calcularDistancia(oponente) <= personagem.getAlcanceDeAtaque()) {
            String log = attack(opponentPlayer);
            onTurnEnd.run();
            return log;
        } else {
            moveTowardsOpponent(gm, onTurnEnd);
            return getNome() + " está se aproximando do oponente...";
        }
    }

    /**
     * Lógica para a dificuldade "Normal". Usa poderes especiais sob certas condições.
     * @param gm O gerenciador do grid.
     * @param opponentPlayer O jogador oponente.
     * @param onTurnEnd O callback de final de turno.
     * @return Uma string de log.
     */
    private String normalAction(GridManager gm, Player opponentPlayer, Runnable onTurnEnd) {
        if ("Guerreiro".equals(personagem.getTipo()) && !usouPoderEspecial && personagem.calcularDistancia(oponente) <= 1) {
            usouPoderEspecial = true;
            String log = personagem.usarPoderEspecial(oponente);
            onTurnEnd.run();
            return log;
        }
        if ("Mago".equals(personagem.getTipo()) && personagem.getPontosDeVida() < 40 && oponente.getPontosDeVida() > 60) {
            String log = personagem.usarPoderEspecial(oponente);
            onTurnEnd.run();
            return log;
        }
        if ("Arqueiro".equals(personagem.getTipo()) && !usouPoderEspecial) {
            int alcance = personagem.getAlcanceDeAtaque();
            int distancia = personagem.calcularDistancia(oponente);

            if (distancia == alcance + 1) {
                usouPoderEspecial = true;
                String log = personagem.usarPoderEspecial(oponente);
                onTurnEnd.run();
                return log;
            }
        }
        return easyAction(gm, opponentPlayer, onTurnEnd);
    }

    /**
     * Lógica para a dificuldade "Difícil". Usa uma IA tática com análise de risco (Minimax de 1 nível).
     * @param gm O gerenciador do grid.
     * @param opponentPlayer O jogador oponente.
     * @param onTurnEnd O callback de final de turno.
     * @return Uma string de log.
     */
    private String hardAction(GridManager gm, Player opponentPlayer, Runnable onTurnEnd) {
        // Instinto Finalizador
        if (personagem.calcularDistancia(oponente) <= personagem.getAlcanceDeAtaque()) {
            if (oponente.getPontosDeDefesa() + oponente.getPontosDeVida() <= personagem.getForcaDeAtaque()) {
                onTurnEnd.run();
                return attack(opponentPlayer);
            }
        }
        if ("Guerreiro".equals(personagem.getTipo()) && !usouPoderEspecial && personagem.calcularDistancia(oponente) <= 1) {
            if (oponente.getPontosDeDefesa() + oponente.getPontosDeVida() <= 30) {
                usouPoderEspecial = true;
                String log = personagem.usarPoderEspecial(oponente);
                onTurnEnd.run();
                return log;
            }
        }

        List<ActionScore> possibleActions = new ArrayList<>();
        char[] directions = {'C', 'B', 'E', 'D'};

        // Avalia ação de ataque
        if (personagem.calcularDistancia(oponente) <= personagem.getAlcanceDeAtaque()) {
            possibleActions.add(new ActionScore("ATTACK", ' ', evaluateActionWithOpponentResponse(gm, "ATTACK", ' ')));
        }

        // Avalia ação de defesa com bônus tático para o guerreiro
        double defendScore = evaluateActionWithOpponentResponse(gm, "DEFEND", ' ');
        // Defender em posição de ataque é uma ótima jogada de preparação.
        if ("Guerreiro".equals(personagem.getTipo()) && personagem.calcularDistancia(oponente) <= 1) {
            defendScore += 40; // Bônus heurístico massivo para incentivar a defesa em combate corpo a corpo.
        }
        possibleActions.add(new ActionScore("DEFEND", ' ', defendScore));

        // Avalia poder
        if (!usouPoderEspecial || "Mago".equals(personagem.getTipo())) {
            double powerScore = evaluatePowerAction(gm);
            if (powerScore > Double.MIN_VALUE + 1) {
                possibleActions.add(new ActionScore("POWER", ' ', powerScore));
            }
        }

        // Avalia movimentos
        for (char dir : directions) {
            if (isValidMove(gm, dir)) {
                int currentDist = personagem.calcularDistancia(oponente);
                int newX = personagem.getPosicaoX(), newY = personagem.getPosicaoY();
                switch (dir) { case 'C' -> newY--; case 'B' -> newY++; case 'E' -> newX--; case 'D' -> newX++; }
                int futureDist = Math.max(Math.abs(newX - oponente.getPosicaoX()), Math.abs(newY - oponente.getPosicaoY()));

                double score = evaluateActionWithOpponentResponse(gm, "MOVE", dir);

                if ("Arqueiro".equals(personagem.getTipo()) && currentDist <= 2 && futureDist > currentDist) {
                    score += 50; // Bônus massivo para se afastar de uma ameaça próxima
                }
                possibleActions.add(new ActionScore("MOVE", dir, score));
            }
        }

        ActionScore bestAction = possibleActions.stream()
                .max(Comparator.comparingDouble(ActionScore::score))
                .orElse(new ActionScore("DEFEND", ' ', -9999));

        // Executa a ação
        switch (bestAction.action()) {
            case "ATTACK":
                onTurnEnd.run();
                return attack(opponentPlayer);
            case "POWER":
                if ("Guerreiro".equals(personagem.getTipo()) || "Arqueiro".equals(personagem.getTipo())) {
                    usouPoderEspecial = true;
                }
                String log = personagem.usarPoderEspecial(oponente);
                onTurnEnd.run();
                return log;
            case "MOVE":
                int x = personagem.getPosicaoX(), y = personagem.getPosicaoY();
                switch (Character.toUpperCase(bestAction.direction())) {
                    case 'C' -> y--; case 'B' -> y++;
                    case 'E' -> x--; case 'D' -> x++;
                }
                gm.movePersonagem(personagem, x, y, onTurnEnd);
                return getNome() + " moveu-se taticamente.";
            case "DEFEND":
            default:
                personagem.restoreDefense();
                onTurnEnd.run();
                return getNome() + " está em postura defensiva!";
        }
    }


    /**
     * Avalia uma ação do bot, simulando também a melhor resposta do oponente a essa ação.
     * @param gm O gerenciador do grid.
     * @param botAction A ação do bot a ser avaliada.
     * @param direction A direção, se a ação for de movimento.
     * @return A pontuação do pior cenário possível para o bot após a resposta do oponente.
     */
    private double evaluateActionWithOpponentResponse(GridManager gm, String botAction, char direction) {
        SimulatedState stateAfterBotMove = simulateBotAction(botAction, direction);
        double worstScoreForBot = Double.MAX_VALUE;
        String[] opponentActions = {"ATTACK", "DEFEND", "POWER"};

        for (String oppAction : opponentActions) {
            SimulatedState finalState = simulateOpponentAction(stateAfterBotMove, oppAction);
            double finalScore = evaluateState(finalState);
            if (finalScore < worstScoreForBot) {
                worstScoreForBot = finalScore;
            }
        }
        return worstScoreForBot;
    }

    /**
     * Função heurística que atribui uma pontuação a um estado de jogo simulado.
     * @param state O estado do jogo a ser avaliado.
     * @return Uma pontuação numérica representando a vantagem do bot naquele estado.
     */
    private double evaluateState(SimulatedState state) {
        double healthWeight = 2.0;
        if (state.myHealth < 35) {
            healthWeight = 3.0;
        }

        if (state.oppHealth <= 0) return Double.MAX_VALUE;
        if (state.myHealth <= 0) return Double.MIN_VALUE;

        double myEffectiveHealth = state.myHealth + (state.myDefense * 0.7);
        double oppEffectiveHealth = state.oppHealth + (state.oppDefense * 0.7);
        double healthScore = (myEffectiveHealth - oppEffectiveHealth) * healthWeight;

        double positionScore = 0;
        int dist = Math.max(Math.abs(state.myX - oponente.getPosicaoX()), Math.abs(state.myY - oponente.getPosicaoY()));
        boolean iAmInRange = dist <= personagem.getAlcanceDeAtaque();
        if ("Guerreiro".equals(personagem.getTipo())) positionScore -= dist * 2;
        else if (iAmInRange) positionScore += dist;
        else positionScore -= dist * 3;

        return healthScore + positionScore;
    }

    /**
     * Simula o resultado de uma ação potencial do bot para gerar um estado futuro.
     * @param action A ação a ser simulada ("ATTACK", "DEFEND", "POWER", "MOVE").
     * @param direction A direção do movimento (se a ação for "MOVE").
     * @return Um objeto SimulatedState representando o resultado da ação.
     */
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

    /**
     * Simula a resposta do oponente a um estado de jogo hipotético.
     * @param initialState O estado do jogo DEPOIS da ação simulada do bot.
     * @param oppAction A ação que o oponente está simulando.
     * @return Um objeto SimulatedState representando o resultado final.
     */
    private SimulatedState simulateOpponentAction(SimulatedState initialState, String oppAction) {
        int myHealth = initialState.myHealth();
        int myDefense = initialState.myDefense();
        int oppHealth = initialState.oppHealth();
        int oppDefense = initialState.oppDefense();
        int myX = initialState.myX();

        int dist = Math.max(Math.abs(myX - oponente.getPosicaoX()), Math.abs(initialState.myY() - oponente.getPosicaoY()));

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
                        if (dist <= 1) {
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
        return new SimulatedState(myHealth, myDefense, oppHealth, oppDefense, myX, initialState.myY());
    }

    /**
     * Verifica se um movimento em uma determinada direção é válido (dentro do grid e para uma célula vazia).
     * @param gm O gerenciador do grid.
     * @param dir A direção do movimento ('C', 'B', 'E', 'D').
     * @return true se o movimento for válido, false caso contrário.
     */
    private boolean isValidMove(GridManager gm, char dir) {
        int x = personagem.getPosicaoX();
        int y = personagem.getPosicaoY();
        switch (dir) {
            case 'C' -> y--;
            case 'B' -> y++;
            case 'E' -> x--;
            case 'D' -> x++;
        }
        if (x < 0 || x >= GridManager.GRID_SIZE || y < 0 || y >= GridManager.GRID_SIZE) {
            return false;
        }
        return !gm.isCellOccupied(x, y);
    }

    /**
     * Executa a ação de ataque do bot contra o oponente.
     * @param opponentPlayer O jogador oponente.
     * @return Uma string de log descrevendo o ataque.
     */
    public String attack(Player opponentPlayer) {
        Personagem alvo = opponentPlayer.getPersonagem();
        int danoTotal = personagem.getForcaDeAtaque();
        alvo.sofrerDano(danoTotal);
        return getNome() + " atacou " + alvo.getNome() + " com força de " + danoTotal + ".";
    }

    /**
     * Move o bot em direção ao oponente. Usado nas dificuldades mais fáceis.
     * @param gm O gerenciador do grid.
     * @param onTurnEnd O callback de final de turno a ser passado para o método de movimento.
     */
    private void moveTowardsOpponent(GridManager gm, Runnable onTurnEnd) {
        int x = personagem.getPosicaoX();
        int y = personagem.getPosicaoY();
        int ex = oponente.getPosicaoX();
        int ey = oponente.getPosicaoY();
        int currentX = x;

        if (x < ex) x++;
        else if (x > ex) x--;
        else if (y < ey) y++;
        else if (y > ey) y--;

        if (!gm.movePersonagem(personagem, x, y, onTurnEnd)) {
            if (currentX < ex || currentX > ex) {
                if (y < ey) gm.movePersonagem(personagem, currentX, y + 1, onTurnEnd);
                else gm.movePersonagem(personagem, currentX, y - 1, onTurnEnd);
            } else {
                if (x < ex) gm.movePersonagem(personagem, x + 1, y, onTurnEnd);
                else gm.movePersonagem(personagem, x - 1, y, onTurnEnd);
            }
        }
    }

    private double evaluatePowerAction(GridManager gm) {
        String tipo = personagem.getTipo();
        switch(tipo) {
            case "Arqueiro":
                int alcance = personagem.getAlcanceDeAtaque();
                int distancia = personagem.calcularDistancia(oponente);
                if (distancia > alcance && distancia <= alcance + 1) {
                    SimulatedState stateAfterPower = simulateBotAction("ATTACK_BOOSTED", ' ');
                    return evaluateState(stateAfterPower);
                }
                return Double.MIN_VALUE;
            case "Guerreiro":
                double baseScore = evaluateActionWithOpponentResponse(gm, "POWER", ' ');
                if (oponente.getPontosDeDefesa() > personagem.getForcaDeAtaque() || oponente.getPontosDeVida() < 25) {
                    baseScore += 30;
                }
                return baseScore;
            default: // Mago
                return evaluateActionWithOpponentResponse(gm, "POWER", ' ');
        }
    }

}