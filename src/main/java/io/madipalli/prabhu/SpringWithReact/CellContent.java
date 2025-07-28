package io.madipalli.prabhu.SpringWithReact;

public interface CellContent {
    boolean isSnakeBody();

    class SnakeHead implements CellContent {

        public static final SnakeHead INSTANCE = new SnakeHead();

        private SnakeHead() {

        }
        @Override
        public boolean isSnakeBody() {
            return true;
        }

        @Override
        public String toString() {
            return "➤";
        }
    }

    class SnakeBody implements CellContent {
        @Override
        public boolean isSnakeBody() {
            return true;
        }

        @Override
        public String toString() {
            return "▀";
        }
    }
}
