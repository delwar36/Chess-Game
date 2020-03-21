package delwar.javaproject.chess.controll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ChessEngine {

    private static final Random random = new Random();

    public static Chessboard compute(Chessboard chessboard) {
        long time = System.currentTimeMillis();

        List<CThread> threads = new ArrayList<>();
        for (List<Chessboard> partPossibleMoves : splitList(getPossibleMoves(chessboard), Runtime.getRuntime().availableProcessors())) {
            CThread thread = new CThread(chessboard.endGame, partPossibleMoves);
            threads.add(thread);
            thread.start();
        }

        int bestValue = Integer.MIN_VALUE;
        ArrayList<Chessboard> bestMoves = new ArrayList<>();
        for (CThread thread : threads) {
            CThread.Result result = thread.getResult();
            if (result.bestValue > bestValue) {
                bestValue = result.bestValue;
                bestMoves = result.bestMoves;
            } else if (result.bestValue == bestValue) {
                bestMoves.addAll(result.bestMoves);
            }
        }

        System.out.println("compute took " + (System.currentTimeMillis() - time)+ " ms, " + bestMoves.size() + " move(s) with score " + bestValue);
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private static <T> ArrayList<List<T>> splitList(ArrayList<T> list, int parts) {
        parts = Math.min(parts, list.size());
        ArrayList<List<T>> lists = new ArrayList<>(parts);

        int partSize = list.size() / parts;

        for (int i = 0; i < parts; i++)
            lists.add(list.subList(i * partSize, i + 1 == parts ? list.size() : (i + 1) * partSize));

        return lists;
    }

    private static ArrayList<Chessboard> getPossibleMoves(Chessboard chessboard){
        ArrayList<Chessboard> possibleMoves = new ArrayList<>();

        for (int i1 = 0; i1 < 8; i1++) {
            for (int i2 = 0; i2 < 8; i2++) {
                ChessPiece chessPiece = chessboard.getChessPiece(i1, i2);
                if (chessPiece != null && chessPiece.playerColor == chessboard.playerColor) {
                    for (int[] m : chessboard.getPossibleMoves(i1, i2)) {
                        Chessboard move = chessboard.move(new int[] { i1, i2 }, m);
                        if (move.promotion)
                            for (ChessPieceType type : ChessPieceType.POSITIONS_PROMOTION)
                                possibleMoves.add(move.promotion(type));
                        else
                            possibleMoves.add(move);
                    }
                }
            }
        }

        return possibleMoves;
    }

    // negamax algorithm with alpha beta pruning https://en.wikipedia.org/wiki/Negamax
    private static int negamax(Chessboard chessboard, int depth, int alpha, int beta) {
        if (chessboard.isDraw())
            return chessboard.playerColor.sign * computeValue(chessboard) / 2;
        if (chessboard.isWin(PlayerColor.WHITE))
            return chessboard.playerColor.sign * (computeValue(chessboard) + 1000);
        if (chessboard.isWin(PlayerColor.BLACK))
            return chessboard.playerColor.sign * (computeValue(chessboard) - 1000);
        if (depth == 0)
            return chessboard.playerColor.sign * computeValue(chessboard);

        int bestValue = Integer.MIN_VALUE;

        for (Chessboard c : getPossibleMoves(chessboard)) {
            int value = -negamax(c, depth - 1, -beta, -alpha);
            bestValue = Math.max(bestValue, value);

            alpha = Math.max(alpha, value);
            if (alpha >= beta)
                break;
        }

        return bestValue;
    }


    // https://chessprogramming.wikispaces.com/Simplified+evaluation+function
    public static int computeValue(Chessboard chessboard) {
        int value = 0;
        for (int i1 = 0; i1 < 8; i1++) {
            for (int i2 = 0; i2 < 8; i2++) {
                ChessPiece chessPiece = chessboard.getChessPiece(i1, i2);
                if (chessPiece != null) {
                    value += (chessPiece.chessPieceType.value
                            + chessPiece.chessPieceType.getSquareValues(chessboard.endGame)[i1][chessPiece.playerColor == PlayerColor.WHITE ? i2 : 7 - i2])
                            * chessPiece.playerColor.sign;
                }
            }
        }

        return value;
    }


    // unused
    /*private static int[][] computeThreatenedMatrix(Chessboard chessboard) {
        int[][] threatenedMatrix = new int[8][8];
        for (int i1 = 0; i1 < 8; i1++) {
            for (int i2 = 0; i2 < 8; i2++) {
                ChessPiece chessPiece = chessboard.getChessPiece(i1, i2);
                if (chessPiece != null) {
                    for (int[] a : chessboard.getThreatenedFields(i1, i2)) {
                        if (chessPiece.playerColor == chessboard.playerColor)
                            threatenedMatrix[a[0]][a[1]] += chessPiece.chessPieceType.value;
                        else
                            threatenedMatrix[a[0]][a[1]] -= chessPiece.chessPieceType.value;
                    }
                }
            }
        }
        return threatenedMatrix;
    }*/

    private static final class CThread extends Thread {

        private final boolean endGame;
        private final List<Chessboard> moves;
        private ArrayList<Chessboard> bestMoves = new ArrayList<>();
        private int bestValue = Integer.MIN_VALUE;

        private CThread(boolean endGame, List<Chessboard> moves) {
            this.endGame = endGame;
            this.moves = moves;
        }

        private Result getResult() {
            try {
                join();
            } catch (InterruptedException ignored) {}
            return new Result(bestValue, bestMoves);
        }

        @Override
        public void run() {
            for (Chessboard c : moves) {
                int value = -negamax(c, endGame ? 3 : 2, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
                if (value > bestValue) {
                    bestValue = value;
                    bestMoves = new ArrayList<>();
                    bestMoves.add(c);
                } else if (value == bestValue) {
                    bestMoves.add(c);
                }
            }
        }

        private static final class Result {

            final int bestValue;
            final ArrayList<Chessboard> bestMoves;

            private Result(int bestValue, ArrayList<Chessboard> bestMoves) {
                this.bestValue = bestValue;
                this.bestMoves = bestMoves;
            }
        }
    }
}