package gui;

import core.Board;
import core.StoneColor;
import core.exception.UnsupportedFileFormatException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {

    private Sprite sprites;

    private final Dimension MAIN_WINDOW_DIMENSION = new Dimension(1366, 768);
    private final int UTILITY_PANEL_WIDTH = 410;
    private final int BOARD_PANEL_SIZE = 696;

    private final Dimension BOARD_PANEL_DIMENSION = new Dimension(BOARD_PANEL_SIZE, BOARD_PANEL_SIZE);
    private final Dimension UTILITY_PANEL_DIMENSION = new Dimension(UTILITY_PANEL_WIDTH, BOARD_PANEL_SIZE);

    // JPanel for start screen
    private JPanel jStartScreenPanel;

    // JPanel for board
    private JPanel jBoard;

    private JPanel jUtility;
    private JPanel jBlackPlayer;
    private JPanel jWhitePlayer;
    private JLabel blackPlayerCaptures;
    private JLabel whitePlayerCaptures;

    // Board that handles game logic
    private Board board;
    private boolean isGameGoing;

    private JButton[][] jIntersections;

    public GUI() {
        this.setLayout(new GridBagLayout());
        isGameGoing = false;
        initMenu();
        initWindow();
        showStartScreen();
        revalidate();
    }

    private void initWindow() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Summer Go");
        setSize(MAIN_WINDOW_DIMENSION);
        setResizable(false);
        setVisible(true);
    }

    private void showStartScreen() {
        GridLayout startScreenLayout = new GridLayout(3, 1, 0, 50);
        
        jStartScreenPanel = new JPanel(startScreenLayout);

        Dimension startScreenButtonDimension = new Dimension(200, 75);

        JButton jNewGameButton = new JButton("New Game");
        jNewGameButton.addActionListener(getNewGameListener());
        jNewGameButton.setMinimumSize(startScreenButtonDimension);
        jNewGameButton.setPreferredSize(startScreenButtonDimension);
        jStartScreenPanel.add(jNewGameButton);

        JButton jLoadGameButton = new JButton("Load Game");
        jLoadGameButton.addActionListener(getLoadGameListener());
        jLoadGameButton.setMinimumSize(startScreenButtonDimension);
        jLoadGameButton.setPreferredSize(startScreenButtonDimension);
        jStartScreenPanel.add(jLoadGameButton);

        JButton jExitGameButton = new JButton("Exit Game");
        jExitGameButton.addActionListener(getExitGameListener());
        jExitGameButton.setMinimumSize(startScreenButtonDimension);
        jExitGameButton.setPreferredSize(startScreenButtonDimension);
        jStartScreenPanel.add(jExitGameButton);

        getContentPane().add(jStartScreenPanel);
    }

    private ActionListener getNewGameListener() {
        return (actionEvent -> {
            JPanel inputPanel = new JPanel(new GridLayout(4, 2));

            JLabel playerOneNameLabel = new JLabel("Player 1: ", JLabel.CENTER);
            inputPanel.add(playerOneNameLabel);
            JTextField playerOneNameTextField = new JTextField();
            inputPanel.add(playerOneNameTextField);

            JLabel playerTwoNameLabel = new JLabel("Player 2: ", JLabel.CENTER);
            inputPanel.add(playerTwoNameLabel);
            JTextField playerTwoNameTextField = new JTextField();
            inputPanel.add(playerTwoNameTextField);
            // Board size
            JLabel boardSizeLabel = new JLabel("Size: ", JLabel.CENTER);
            inputPanel.add(boardSizeLabel);
            JComboBox<String> boardSizeComboBox = new JComboBox<>();
            boardSizeComboBox.addItem("9x9");
            boardSizeComboBox.addItem("13x13");
            boardSizeComboBox.addItem("19x19");
            inputPanel.add(boardSizeComboBox);

            // Komi
            JLabel komiLabel = new JLabel("Komi: ", JLabel.CENTER);
            inputPanel.add(komiLabel);
            JTextField komiSizeTextField = new JTextField();
            inputPanel.add(komiSizeTextField);

            int boardSizeChoose = JOptionPane.showConfirmDialog(this,
                    inputPanel,
                    "New game",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (boardSizeChoose == JOptionPane.OK_OPTION) {
                int boardSize = Integer.parseInt(((String) boardSizeComboBox.getSelectedItem()).split("x")[0]);
                double komi = Double.parseDouble(komiSizeTextField.getText());
                this.board = new Board(boardSize, komi, playerOneNameTextField.getText(), playerTwoNameTextField.getText());
                newGame(boardSize, komi);
            }
        });
    }

    private ActionListener getSaveGameListener() {
        return (actionEvent -> {
            if (this.board == null)
                return;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a directory to save game: ");
            // TODO
            // fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    this.board.saveGame(file);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null,
                            "<html>Problem saving to file: </b>" + file.getName() + "</b></html>",
                            "Saving error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private ActionListener getLoadGameListener() {
        return (actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a file containing a game: ");
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    newGame(Board.loadGame(file));
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null,
                            "<html>Can't open file <b>" + exc.getMessage() + "</b></html>",
                            "Can't open file",
                            JOptionPane.ERROR_MESSAGE);
                } catch (UnsupportedFileFormatException exc) {
                    JOptionPane.showMessageDialog(null,
                            "<html>File <b>" + exc.getMessage() + "</b> has invalid format</html>",
                            "Invalid file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private ActionListener getExitGameListener() {
        return (actionEvent -> {
            String[] options = { "Yes", "No" };
            int returnValue = JOptionPane.showOptionDialog(this,
                    "<html>Are you sure you want to exit?<br>All unsaved progress will be lost</html>",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (returnValue == JOptionPane.OK_OPTION)
                System.exit(0);
        });
    }

    private void initMenu() {
        // Menu Bar
        JMenuBar jMenuBar = new JMenuBar();

        JMenu jMenuGame = new JMenu("Game");
        JMenu jMenuHelp = new JMenu("Help");
        JMenu jMenuAbout = new JMenu("About");

        // Game menu
        // Game Menu
        JMenuItem jGameNew = new JMenuItem("New Game");
        jGameNew.setAccelerator(KeyStroke.getKeyStroke("control N"));

        jGameNew.addActionListener(getNewGameListener());

        JMenuItem jGameSave = new JMenuItem("Save Game");
        jGameSave.setAccelerator(KeyStroke.getKeyStroke("control S"));
        jGameSave.addActionListener(getSaveGameListener());

        JMenuItem jGameLoad = new JMenuItem("Load Game");
        jGameLoad.setAccelerator(KeyStroke.getKeyStroke("control O"));
        jGameLoad.addActionListener(getLoadGameListener());

        JMenuItem jGameExit = new JMenuItem("Exit");
        jGameExit.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        jGameExit.addActionListener(getExitGameListener());

        jMenuGame.add(jGameNew);
        jMenuGame.addSeparator();
        jMenuGame.add(jGameSave);
        jMenuGame.add(jGameLoad);
        jMenuGame.addSeparator();
        jMenuGame.add(jGameExit);

        // Help menu
        jMenuHelp.setMnemonic(KeyEvent.VK_F1);
        jMenuHelp.addActionListener(actionEvent -> {
            // TODO
        });

        // About menu
        jMenuAbout.addActionListener(actionEvent -> {
            // TODO
        });

        // Getting it all together
        jMenuBar.add(jMenuGame);
        jMenuBar.add(jMenuHelp);
        jMenuBar.add(jMenuAbout);

        // Setting the menu bar
        this.setJMenuBar(jMenuBar);
    }

    private void initBoard() {
        jBoard = new JPanel();
        if (board != null) {
            int boardSize = board.getBoardSize();
            jBoard.setLayout(new GridLayout(boardSize, boardSize));
            jIntersections = new JButton[boardSize][boardSize];
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    jIntersections[x][y] = new JButton(sprites.getIcon(StoneColor.EMPTY, x, y, boardSize));
                    jIntersections[x][y].setEnabled(true);
                    jIntersections[x][y].setBorder(BorderFactory.createEmptyBorder());
                    jIntersections[x][y].setContentAreaFilled(false);
                    jIntersections[x][y].setMinimumSize(new Dimension(BOARD_PANEL_SIZE / boardSize, BOARD_PANEL_SIZE / boardSize));
                    jIntersections[x][y].setPreferredSize(new Dimension(BOARD_PANEL_SIZE / boardSize, BOARD_PANEL_SIZE / boardSize));
                    jIntersections[x][y].setMaximumSize(new Dimension(BOARD_PANEL_SIZE / boardSize, BOARD_PANEL_SIZE / boardSize));

                    int finalX = x;
                    int finalY = y;
                    jIntersections[x][y].addActionListener(actionPerformed -> {
                        if (!isGameGoing) {
                            board.flipDeadStones(finalX, finalY);
                        }
                        if (board.makeMove(finalX, finalY)) {
                            updateBoard();
                            updateUtility();
                        }
                    });
                    jBoard.add(jIntersections[x][y], x, y);
                }
            }

            jBoard.setMinimumSize(BOARD_PANEL_DIMENSION);
            jBoard.setPreferredSize(BOARD_PANEL_DIMENSION);
            jBoard.setMaximumSize(BOARD_PANEL_DIMENSION);
            jBoard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            getContentPane().add(jBoard);
        }
    }

    private void initUtility() {

        JPanel jUtilityPanel = new JPanel(new GridBagLayout());
        jUtilityPanel.setPreferredSize(UTILITY_PANEL_DIMENSION);
        jUtilityPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 5;
        constraints.gridheight = 5;
        constraints.insets = new Insets(20, 20, 20, 20);

        // Players info and avatars
        JPanel jPlayerInfo = new JPanel(new GridLayout(1, 2, 60, 0));

        // Black player
        jBlackPlayer = new JPanel(new GridLayout(3, 1));
        JLabel jBlackPlayerName = new JLabel(board.getPlayerOne().getName() + "(Black)", JLabel.CENTER);
        JLabel jBlackPlayerAvatar = new JLabel(new ImageIcon("sprites/black_avatar.png"));
        blackPlayerCaptures = new JLabel("0", sprites.b_captured, JLabel.CENTER);
        jBlackPlayer.add(jBlackPlayerName);
        jBlackPlayer.add(jBlackPlayerAvatar);
        jBlackPlayer.add(blackPlayerCaptures);
        jPlayerInfo.add(jBlackPlayer);

        // White player
        jWhitePlayer = new JPanel(new GridLayout(3, 1));
        JLabel jWhitePlayerName = new JLabel(board.getPlayerTwo().getName() + "(White)", JLabel.CENTER);
        JLabel jWhitePlayerAvatar = new JLabel(new ImageIcon("sprites/white_avatar.png"));
        whitePlayerCaptures = new JLabel("0", sprites.w_captured, JLabel.CENTER);
        jWhitePlayer.add(jWhitePlayerName);
        jWhitePlayer.add(jWhitePlayerAvatar);
        jWhitePlayer.add(whitePlayerCaptures);
        jPlayerInfo.add(jWhitePlayer);

        // Captured stones

        jUtilityPanel.add(jPlayerInfo, constraints);

        JPanel jGameControls = new JPanel(new GridLayout(1, 4, 10, 50));
        JButton jUndoButton = new JButton("Undo");
        jUndoButton.addActionListener(actionEvent -> {
            board.undo();
            updateBoard();
            updateUtility();
        });

        JButton jRedoButton = new JButton("Redo");
        jRedoButton.addActionListener(actionEvent -> {
            board.redo();
            updateBoard();
            updateUtility();
        });

        JButton jPassButton = new JButton("Pass");
        jPassButton.addActionListener(actionEvent -> {
            board.makePass();
            updateBoard();
            updateUtility();
            if (board.isGameOver()) {
                JOptionPane.showMessageDialog(this,
                        "<html>Mark dead stones on board by clicking on them.<br>" +
                                "When all dead stones are marked click on <b>Score</b></html>");
                initScoring();
            }
        });

        JButton jResignButton = new JButton("Resign");
        jResignButton.addActionListener(actionEvent -> {
            String[] options = { "Yes", "No" };
            int returnValue = JOptionPane.showOptionDialog(this,
                    "Are you sure?",
                    "Confirm resign",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (returnValue == JOptionPane.OK_OPTION) {
                this.isGameGoing = false;
                // TODO
            }
        });

        JButton jScoreButton = new JButton("Score");
        jScoreButton.addActionListener(actionEvent -> {
            if (!isGameGoing) {
                double result = board.getResult();
                String message = (result > 0) ? "White won by " + result : "Black won by" + result;
                JOptionPane.showMessageDialog(this, message);
            }
        });

        jGameControls.add(jUndoButton);
        jGameControls.add(jRedoButton);
        jGameControls.add(jPassButton);
        jGameControls.add(jResignButton);
        jGameControls.add(jScoreButton);

        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.gridheight = 1;

        jUtilityPanel.add(jGameControls, constraints);

        //jUtilityPanel.setBorder(new BevelBorder(0));

        jUtility = jUtilityPanel;

        getContentPane().add(jUtility);
    }

    // TODO
    private void initScoring() {

    }

    private void updateBoard() {
        for (int x = 0; x < board.getBoardSize(); x++) {
            for (int y = 0; y < board.getBoardSize(); y++) {
                jIntersections[x][y].setIcon(sprites.getIcon(board.getIntersection(x, y).getColor(), x, y, board.getBoardSize()));
            }
        }
    }

    private void updateUtility() {
        StoneColor currentPlayerColor = board.getCurrentPlayer().getColor();
        if (currentPlayerColor == StoneColor.BLACK) {
            jWhitePlayer.setBorder(BorderFactory.createEmptyBorder());
            jBlackPlayer.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        } else {
            jBlackPlayer.setBorder(BorderFactory.createEmptyBorder());
            jWhitePlayer.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }

        blackPlayerCaptures.setText(String.valueOf(board.getBlackCaptures()));
        whitePlayerCaptures.setText(String.valueOf(board.getWhiteCaptures()));
    }

    private void newGame(int boardSize, double komi) {
        if (isGameGoing) {
            getContentPane().remove(jBoard);
            getContentPane().remove(jUtility);
            if (sprites.getBoardSize() != boardSize)
                sprites = new Sprite(boardSize);
        } else {
            getContentPane().remove(jStartScreenPanel);
            sprites = new Sprite(boardSize);
        }
        this.setLayout(new FlowLayout());
        this.isGameGoing = true;
        initBoard();
        add(Box.createRigidArea(new Dimension(10, 0)));
        initUtility();
        revalidate();
        repaint();
    }

    private void newGame(Board board) {
        this.board = board;
        newGame(board.getBoardSize(), board.getKomi());
        updateBoard();
        updateUtility();
    }

    public Board getBoard() {
        return board;
    }
}
