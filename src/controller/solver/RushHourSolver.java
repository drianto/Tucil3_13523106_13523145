// In src/controller/solver/RushHourSolver.java
package controller.solver;

import controller.SolutionResult;
import model.GameState;
import java.util.function.Consumer; 

public abstract class RushHourSolver {
    protected GameState initialState;
    protected int visitedNodesCount;
    protected long executionTime;

    // --- AWAL Perubahan untuk Progress Reporting (FIXED) ---
    // Menggunakan Consumer untuk callback publikasi progres.
    // Consumer akan menerima Integer (visitedNodesCount)
    protected Consumer<Integer> progressEventConsumer;

    /**
     * Mengatur callback (Consumer) yang akan dipanggil untuk mempublikasikan progres.
     * @param consumer Consumer yang akan menerima update progres (misalnya, jumlah node yang dikunjungi).
     */
    public void setProgressEventConsumer(Consumer<Integer> consumer) {
        this.progressEventConsumer = consumer;
    }

    /**
     * Metode helper untuk solver konkret mempublikasikan progres.
     * Ini akan memanggil Consumer yang telah diatur, yang kemudian akan memanggil SwingWorker.publish().
     */
    protected void publishVisitedNodesCount() {
        if (this.progressEventConsumer != null) {
            this.progressEventConsumer.accept(this.visitedNodesCount);
        }
    }

    public RushHourSolver(GameState initialState) {
        this.initialState = initialState;
        this.visitedNodesCount = 0;
        this.executionTime = 0;
    }

    public int getVisitedNodesCount() {
        return this.visitedNodesCount;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public abstract SolutionResult solve();
    public abstract String getName();
}