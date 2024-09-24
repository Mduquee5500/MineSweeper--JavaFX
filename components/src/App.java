// Student name: Mateo Duque Escobar
// Student ID: 300352135 
// File name: App.java
// Description: Basic implementation of the Minesweeper game with a playing thath changes depending onf the difficulty selected. 
//Players can reveal the contents of each tile by clicking on them. 
//The tiles or buttons of the grid reveal numbers indicating nearby mines or a mine itself, represented by a red icon. 
//The game features a smiley face button that resets the game state, which allows users to reset the game when clicked.

import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    public boolean isAlive = true;
    public int bombsQuantity = 10;
    public int bombsQuantityTobeChanged = 10;
    private ImageView smileyImageView;
    private Tile[][] grid;
    public int buttonsQuantity;
    public boolean toBeDisabled;
    public boolean firstClick = false;
    public boolean bombsPlaced = false;
    public int discoveredTilesCount;
    public String disabledButtonStyle = "-fx-opacity: 1; -fx-background-color: #E0E0E0;";
    public static int firstValueForTiles = 8;
    public static int secondValueForTiles = 8;

    private static int flaggedTiles;
    private static Button leftButton;

    private ComboBox<String> difficultyComboBox;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        GridPane gpane = new GridPane();
        VBox board = new VBox();
        stage.setResizable(true);

        // Initialize the ComboBox to select difficulty
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("Easy", "Intermediate", "Hard");
        difficultyComboBox.setValue("Easy"); // Set default value

        // Add a listener to handle changes in the selected difficulty
        difficultyComboBox.setOnAction(event -> {
            String selectedDifficulty = difficultyComboBox.getValue();
            
            if(selectedDifficulty.equals("Easy")) {
                System.out.println("Easy Selected");
                firstValueForTiles = 8;
                secondValueForTiles = 8;
                bombsQuantity = 10;
                buttonsQuantity = 8 * 8;
                startNewGame(gpane, stage);
                System.out.println("Number of buttons: " + buttonsQuantity);
                System.out.println("Number of bombs: " + bombsQuantity);
            } else if(selectedDifficulty.equals("Intermediate")) {
                System.out.println("Intermediate Selected");
                firstValueForTiles = 12;
                secondValueForTiles = 12;
                buttonsQuantity = 12 * 12;
                bombsQuantity = 40;
                startNewGame(gpane, stage);
                System.out.println("Number of buttons: " + buttonsQuantity);
                System.out.println("Number of bombs: " + bombsQuantity);
            } else if(selectedDifficulty.equals("Hard")) {
                System.out.println("Hard Selected");
                firstValueForTiles = 16;
                secondValueForTiles = 16;
                bombsQuantity = 60;
                buttonsQuantity = 16 * 16;
                startNewGame(gpane, stage);
                System.out.println("Number of buttons: " + buttonsQuantity);
                System.out.println("Number of bombs: " + bombsQuantity);
            }
        });

        // Create an HBox container for the ComboBox and align it to the center
        HBox top = new HBox(difficultyComboBox);
        top.setAlignment(Pos.CENTER);

        // Create bleft button for the HBox
        leftButton = new Button();
        leftButton.setText(Integer.toString(bombsQuantityTobeChanged));
        leftButton.setPrefSize(100, 100);
        leftButton.setDisable(true);
        leftButton.setStyle(disabledButtonStyle);

        // Load the image of the happy face png
        Image smileyImage = new Image("file:../minesweeper-basic/face-smile.png");
        smileyImageView = new ImageView(smileyImage);
        smileyImageView.setFitWidth(100);
        smileyImageView.setFitHeight(100);

        // Create the happy face button
        Button smile = new Button();
        smile.setGraphic(smileyImageView);

        // Create right button for the HBox
        Button rightButton = new Button("000");
        rightButton.setPrefSize(100, 100);
        rightButton.setDisable(true);
        rightButton.setStyle(disabledButtonStyle);

        HBox header = new HBox(leftButton, smile, rightButton); // Add the buttons to the HBox
        header.setAlignment(javafx.geometry.Pos.CENTER); // Aligns the HBox to the center
        header.setSpacing(10);
        header.setPadding(new Insets(4));

        // Create a game grid
        Image buttonImage;
        grid = new Tile[firstValueForTiles][secondValueForTiles];
        for (int row = 0; row < firstValueForTiles; row++) {
            for (int col = 0; col < secondValueForTiles; col++) {
                grid[row][col] = new Tile(col, row);
                grid[row][col].setMinSize(50, 50);

                // Load the image for the grid buttons
                buttonImage = new Image("file:../minesweeper-basic/cover.png");
                ImageView buttonImageView = new ImageView(buttonImage);
                buttonImageView.setFitWidth(50);
                buttonImageView.setFitHeight(50);

                // Set the image as the button's graphic
                grid[row][col].setGraphic(buttonImageView);
                gpane.add(grid[row][col], col, row);
            }
        }

        // Number of buttons or tiles in the grid
        buttonsQuantity = firstValueForTiles * secondValueForTiles;

        // Handle clicks on the happy face button
        smile.setOnAction(event -> startNewGame(gpane, stage));

        board.getChildren().addAll(top, header, gpane);

        Scene scene = new Scene(board);
        stage.setScene(scene);
        stage.setTitle("Minesweeper");
        stage.show();
    }

    private void startNewGame(GridPane gpane, Stage primaryStage) {
        isAlive = true;
        flaggedTiles = 0;
        toBeDisabled = false;
        firstClick = false;
        bombsPlaced = false;
        flaggedTiles = 0;
        discoveredTilesCount = 0;
    
        // Restart face image
        Image smileyImage = new Image("file:../minesweeper-basic/face-smile.png");
        smileyImageView.setImage(smileyImage);
    
        // Restart bomb counter
        bombsQuantityTobeChanged = bombsQuantity;
        leftButton.setText(Integer.toString(bombsQuantityTobeChanged));
    
        // Restart grid size
        grid = new Tile[firstValueForTiles][secondValueForTiles];
    
         // Calculates grid size by its elements to resize it
        double gridWidth = secondValueForTiles * 51.1;
        double gridHeight = firstValueForTiles * 50;
        double additionalHeight = 180;

        // Window size
        primaryStage.setWidth(gridWidth);
        primaryStage.setHeight(gridHeight + additionalHeight);

        // Clear current gridpane
        gpane.getChildren().clear();

        // Adds the new tiles and elements to the gridpane
        for (int row = 0; row < firstValueForTiles; row++) {
            for (int col = 0; col < secondValueForTiles; col++) {
                grid[row][col] = new Tile(col, row);
                grid[row][col].setMinSize(50, 50);
    
                Image buttonImage = new Image("file:../minesweeper-basic/cover.png");
                ImageView buttonImageView = new ImageView(buttonImage);
                buttonImageView.setFitWidth(50);
                buttonImageView.setFitHeight(50);
    
                grid[row][col].setGraphic(buttonImageView);
    
                gpane.add(grid[row][col], col, row);
            }
        }
    
        // Restart the buttons
        for (int row = 0; row < firstValueForTiles; row++) {
            for (int col = 0; col < secondValueForTiles; col++) {
                ImageView buttonImageView = (ImageView) grid[row][col].getGraphic();
                Image buttonImage = new Image("file:../minesweeper-basic/cover.png");
                buttonImageView.setImage(buttonImage);
    
                grid[row][col].setDisable(false);

                grid[row][col].setBomb(false);
                grid[row][col].setZero(false);
                grid[row][col].setOne(false);
                grid[row][col].setTwo(false);
                grid[row][col].setThree(false);
            }
        }
    }
    
    class Tile extends Button {
        private int x;
        private int y;
        private boolean isBomb;
        private boolean isZero;
        private boolean isOne;
        private boolean isTwo;
        private boolean isThree;
        private boolean discovered;
    
        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
            this.setStyle("-fx-focus-color:transparent;");
            this.isBomb = false;
            this.isZero = false;
            this.isOne = false;
            this.isTwo = false;
            this.isThree = false;
            this.discovered = false;
            this.setPadding(Insets.EMPTY);
    
            // Handle left clicks on the tile
            this.setOnMouseClicked(event -> {
                if (!discovered) {
                    // If the tile has not been discovered yet, mark it as discovered and update the appearance
                    discovered = true;
                    discoveredTilesCount++;
                }
    
                if (event.getButton() == MouseButton.PRIMARY) {
    
                    if(!firstClick) {
                        this.setZero(true);
                        firstClick = true;
                    }
    
                    // Set bomb positions only on the first click
                    if (firstClick && !bombsPlaced) {
                        System.out.println("Setting bombs");
                        for (int i = 0; i < bombsQuantity; i++) {
                            int randomX;
                            int randomY;
    
                            // Generate random coordinates until they don't match a clicked tile
                            do {
                                randomX = (int) (Math.random() * firstValueForTiles);
                                randomY = (int) (Math.random() * secondValueForTiles);
                            } while (grid[randomX][randomY].isBomb() || grid[randomX][randomY].isZero());
    
                            grid[randomX][randomY].setBomb(true);
                        }
                        bombsPlaced = true; // Mark that bombs have been placed
                    }
    
                    // Mark adjacent non-bomb tiles as isOne, isTwo, or isThree
                    if(bombsPlaced) {
                        System.out.println("Verifying bombs");
                        for (int i = 0; i < firstValueForTiles; i++) {
                            for (int j = 0; j < secondValueForTiles; j++) {
                                if (!grid[i][j].isBomb()) {
                                    int bombCount = 0;
    
                                    // Count adjacent bombs for the tile
                                    for (int dx = -1; dx <= 1; dx++) {
                                        for (int dy = -1; dy <= 1; dy++) {
                                            int newX = i + dx;
                                            int newY = j + dy; 
    
                                            // Makes sure that the new coordinates are within the board limits
                                            if (newX >= 0 && newX < firstValueForTiles && newY >= 0 && newY < secondValueForTiles && grid[newX][newY].isBomb()) {
                                                bombCount++;
                                            }
                                        }
                                    }
    
                                    // Set isOne, isTwo, or isThree based on bomb count for the tile
                                    if (bombCount == 1) {
                                        grid[i][j].setOne(true);
                                    } else if (bombCount == 2) {
                                        grid[i][j].setTwo(true);
                                    } else if (bombCount == 3) {
                                        grid[i][j].setThree(true);
                                    } else {
                                        grid[i][j].setZero(true);
                                    }
                                }
                            }
                        }
                    }
    
                    if (this.isZero) {
                        exploreAdjacentEmptyTiles(grid, x, y);
                    }
    
                    // Change button image on click for bombs
                    if (this.isBomb) {
                        Image newImage = new Image("file:../minesweeper-basic/mine-red.png");
                        Image newImageGrey = new Image("file:../minesweeper-basic/mine-grey.png");
                        ImageView imageView = new ImageView(newImage);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        this.setGraphic(imageView);
    
                        isAlive = false;
                        toBeDisabled = true;
    
                        Image smileyImage = new Image("file:../minesweeper-basic/face-dead.png"); 
                        smileyImageView.setImage(smileyImage);
    
                        for (int row = 0; row < firstValueForTiles; row++) {
                            for (int col = 0; col < secondValueForTiles; col++) {
    
                                grid[row][col].setDisable(toBeDisabled);
                                grid[row][col].setStyle(disabledButtonStyle);
                                
    
                                if (grid[row][col].isBomb() && grid[row][col] != this && !((ImageView)grid[row][col].getGraphic()).getImage().getUrl().contains("flag.png")) {
                                    ImageView bombImageView = (ImageView) grid[row][col].getGraphic();
                                    bombImageView.setImage(newImageGrey);
                                }
                            }
                        }
    
                    } else if(this.isOne) {
                        Image newImage = new Image("file:../minesweeper-basic/1.png"); //Loads number 1 image for the buttons clicked
                        ImageView imageView = new ImageView(newImage);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        this.setGraphic(imageView);
    
                    } else if(this.isTwo) {
                        Image newImage = new Image("file:../minesweeper-basic/2.png"); //Loads number 2 for the buttons clicked
                        ImageView imageView = new ImageView(newImage);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        this.setGraphic(imageView);
    
                    } else if(this.isThree) {
                        Image newImage = new Image("file:../minesweeper-basic/3.png"); //Loads number 3 image for the buttons clicked
                        ImageView imageView = new ImageView(newImage);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        this.setGraphic(imageView);
    
                    } else {
                        Image newImage = new Image("file:../minesweeper-basic/0.png"); //Loads uncovered image for the buttons clicked
                        ImageView imageView = new ImageView(newImage);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        this.setGraphic(imageView);
                    }
    
                    // Count the number of discovered tiles after each click
                    int discoveredTilesCount = 0;
                    for (int row = 0; row < firstValueForTiles; row++) {
                        for (int col = 0; col < secondValueForTiles; col++) {
                            ImageView currentImageView = (ImageView) grid[row][col].getGraphic();
                            String imageUrl = currentImageView.getImage().getUrl();
                            if (!imageUrl.contains("cover.png") && !imageUrl.contains("flag.png")) {
                                discoveredTilesCount++;
                                System.out.println(discoveredTilesCount);
                            }
                        }
                    }
    
                    //Logic for winning and verifying if the tiles containing bombs are flagged or not in iorder to change the tile image fot the correct one
                    if(discoveredTilesCount == buttonsQuantity - bombsQuantity) {
                        if(flaggedTiles == bombsQuantity) {
                            Image smileyImage = new Image("file:../minesweeper-basic/face-win.png");
                            smileyImageView.setImage(smileyImage);
    
                            toBeDisabled = true;
    
                            for (int row = 0; row < firstValueForTiles; row++) {
                                for (int col = 0; col < secondValueForTiles; col++) {
                                    grid[row][col].setDisable(toBeDisabled);
                                    grid[row][col].setStyle(disabledButtonStyle);
                                }
                            }
    
                            // Change image for buttons still showing "cover.png" for the bomb image
                            for (int row = 0; row < firstValueForTiles; row++) {
                                for (int col = 0; col < secondValueForTiles; col++) {
                                    ImageView currentImageView = (ImageView) grid[row][col].getGraphic();
                                    Image currentImage = currentImageView.getImage();
                                    if (currentImage.getUrl().equals("file:../minesweeper-basic/cover.png")) {
                                        Image newImage = new Image("file:../minesweeper-basic/mine-grey.png");
                                        currentImageView.setImage(newImage);
                                    }
                                }
                            }
                        } else {
                            Image smileyImage = new Image("file:../minesweeper-basic/face-win.png");
                            smileyImageView.setImage(smileyImage);
    
                            toBeDisabled = true;
    
                            for (int row = 0; row < firstValueForTiles; row++) {
                                for (int col = 0; col < secondValueForTiles; col++) {
                                    grid[row][col].setDisable(toBeDisabled);
                                    grid[row][col].setStyle(disabledButtonStyle);
                                }
                            }
    
                            // Change image for buttons still showing "cover.png" for the bomb image
                            for (int row = 0; row < firstValueForTiles; row++) {
                                for (int col = 0; col < secondValueForTiles; col++) {
                                    ImageView currentImageView = (ImageView) grid[row][col].getGraphic();
                                    Image currentImage = currentImageView.getImage();
                                    if (currentImage.getUrl().equals("file:../minesweeper-basic/cover.png")) {
                                        Image newImage = new Image("file:../minesweeper-basic/mine-grey.png");
                                        currentImageView.setImage(newImage);
                                    }
                                }
                            }
                        }
                       
                    }
    
                //Right click event for flagging tiles and un-flagging them
                } else if(event.getButton() == MouseButton.SECONDARY) {
                    if(((ImageView)this.getGraphic()).getImage().getUrl().contains("cover.png")) {
                        if(bombsQuantityTobeChanged > 0) {
                            Image newImage = new Image("file:../minesweeper-basic/flag.png");
                            ImageView imageView = new ImageView(newImage);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            this.setGraphic(imageView);
                            bombsQuantityTobeChanged -= 1;
                            flaggedTiles += 1;
                            App.leftButton.setText(Integer.toString(bombsQuantityTobeChanged));
                        }
                    } else if(((ImageView)this.getGraphic()).getImage().getUrl().contains("flag.png")) {
                        Image newImage = new Image("file:../minesweeper-basic/cover.png");
                        ImageView imageView = new ImageView(newImage);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        this.setGraphic(imageView);
                        bombsQuantityTobeChanged += 1;
                        flaggedTiles -= 1;
                        App.leftButton.setText(Integer.toString(bombsQuantityTobeChanged));
                    }    
                }    
            });
        }
    
        public static void exploreAdjacentEmptyTiles(Tile[][] grid, int x, int y) {
            boolean[][] visited = new boolean[firstValueForTiles][secondValueForTiles]; // Matrix to track visited tiles
            Queue<Tile> queue = new LinkedList<>(); // Queue to store tiles to be visited
            queue.offer(grid[y][x]); // Add the initial tile to the queue
    
            // While there are tiles in the queue, continue exploring
            while (!queue.isEmpty()) {
                Tile currentTile = queue.poll(); // Get the current tile from the queue
                int currentX = currentTile.getX();
                int currentY = currentTile.getY();
    
                // Check if the current tile has not been visited and is not a bomb
                if (!visited[currentY][currentX] && !currentTile.isBomb()) {
                    visited[currentY][currentX] = true; // Mark the tile as visited
    
                    // Check adjacent bombs and change the image accordingly
                    int bombCount = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int newX = currentX + dx; 
                            int newY = currentY + dy; 
    
                            // Makes sure that the new coordinates are within the board limits
                            if (newX >= 0 && newX < firstValueForTiles && newY >= 0 && newY < secondValueForTiles) {
                                if (grid[newY][newX].isBomb()) {
                                    bombCount++;
                                }
                            }
                        }
                    }
    
                    // Change the tile image based on adjacent bomb count
                    ImageView imageView;
                    if (bombCount == 1) {
                        imageView = new ImageView(new Image("file:../minesweeper-basic/1.png"));
                    } else if (bombCount == 2) {
                        imageView = new ImageView(new Image("file:../minesweeper-basic/2.png"));
                    } else if (bombCount == 3) {
                        imageView = new ImageView(new Image("file:../minesweeper-basic/3.png"));
                    } else {
                        imageView = new ImageView(new Image("file:../minesweeper-basic/0.png"));
                    }
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    currentTile.setGraphic(imageView);
    
                    // If the current tile is a zero, add its adjacent tiles to the queue for exploration
                    if (bombCount == 0) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                int newX = currentX + dx;
                                int newY = currentY + dy;
    
                                // Make sure the new coordinates are within the board limits
                                if (newX >= 0 && newX < firstValueForTiles && newY >= 0 && newY < secondValueForTiles) {
                                    queue.offer(grid[newY][newX]); // Add the adjacent tile to the queue
                                }
                            }
                        }
                    }
                }
            }
        }
        
        public int getX() { return x; }
    
        public int getY() { return y; }
    
        public boolean isBomb() { return isBomb; }
    
        public boolean isZero() { return isZero; }
    
        public boolean isOne() { return isOne; }
    
        public boolean isTwo() { return isTwo; }
    
        public boolean isThree() { return isThree; }
    
        public void setBomb(boolean bomb) { isBomb = bomb; }
    
        public void setZero(boolean zero) { isZero = zero; }
    
        public void setOne(boolean one) { isOne = one; }
    
        public void setTwo(boolean two) { isTwo = two; }
    
        public void setThree(boolean three) { isThree = three; }
        
    }    
}