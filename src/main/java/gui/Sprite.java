package gui;

import core.StoneColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

class Sprite {
    private final int IMAGE_SIZE;
    private final int BOARD_SIZE;
    private final ImageIcon background;
    private final ImageIcon p1;
    private final ImageIcon p2;
    private final ImageIcon grid_c;
    private final ImageIcon grid_tl;
    private final ImageIcon grid_tr;
    private final ImageIcon grid_bl;
    private final ImageIcon grid_br;
    private final ImageIcon grid_ts;
    private final ImageIcon grid_bs;
    private final ImageIcon grid_rs;
    private final ImageIcon grid_ls;
    private final ImageIcon star;
    private final ImageIcon w_point;
    private final ImageIcon b_point;
    final ImageIcon b_captured;
    final ImageIcon w_captured;
    // private  final ImageIcon illegal = new ImageIcon(new ImageIcon("sprites/illegal.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

    Sprite(int boardSize) {

        BOARD_SIZE = boardSize;
        // Scaling images according to the board size
        IMAGE_SIZE = 676 / BOARD_SIZE;

        background = new ImageIcon(new ImageIcon("sprites/background.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Black player and captures
        p1 = new ImageIcon(new ImageIcon("sprites/p1.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));
        b_captured = new ImageIcon(new ImageIcon("sprites/p2.png").getImage().getScaledInstance(24, 24, Image.SCALE_FAST));

        // White player and captures
        p2 = new ImageIcon(new ImageIcon("sprites/p2.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));
        w_captured = new ImageIcon(new ImageIcon("sprites/p1.png").getImage().getScaledInstance(24, 24, Image.SCALE_FAST));

        // Center
        grid_c = new ImageIcon(new ImageIcon("sprites/grid_c.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Top left corner
        grid_tl = new ImageIcon(new ImageIcon("sprites/grid_tl.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Top right corner
        grid_tr = new ImageIcon(new ImageIcon("sprites/grid_tr.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Bottom left corner
        grid_bl = new ImageIcon(new ImageIcon("sprites/grid_bl.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Bottom right corner
        grid_br = new ImageIcon(new ImageIcon("sprites/grid_br.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Top side
        grid_ts = new ImageIcon(new ImageIcon("sprites/grid_ts.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Bottom side
        grid_bs = new ImageIcon(new ImageIcon("sprites/grid_bs.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Right side
        grid_rs = new ImageIcon(new ImageIcon("sprites/grid_rs.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Left side
        grid_ls = new ImageIcon(new ImageIcon("sprites/grid_ls.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Star("hoshi")
        star = new ImageIcon(new ImageIcon("sprites/star.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // Black territory
        b_point = new ImageIcon(new ImageIcon("sprites/b_point.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));

        // White territory
        w_point = new ImageIcon(new ImageIcon("sprites/w_point.png").getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_FAST));
    }

    private ImageIcon merge(ArrayList<ImageIcon> images) {

        ArrayList<Float> transparency = new ArrayList<>();
        for (ImageIcon i : images) {
            transparency.add(1.0f);
        }

        return merge(images, transparency);
    }

    private ImageIcon merge(ArrayList<ImageIcon> images, ArrayList<Float> transparency) {
        BufferedImage dest = null;
        Graphics2D destG = null;
        int rule; // This is SRC for the top image, and DST_OVER for the other ones
        float alpha;

        for (int i = 0, size = images.size(); i < size; i++) {
            Image image = images.get(i).getImage();

            rule = AlphaComposite.SRC_OVER; // Default value
            alpha = transparency.get(i);

            if (i == 0) {
                dest = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                destG = dest.createGraphics();

                rule = AlphaComposite.SRC; // Rule for 1st image
            }
            destG.setComposite(AlphaComposite.getInstance(rule, alpha));
            destG.drawImage(image, 0, 0, null);
        }

        return new ImageIcon(dest);
    }

    private ImageIcon getCenterGridIcon(int x, int y, int boardSize) {
        int offset = (boardSize < 10) ? 2 : 3;
        if ((x == offset || x == (boardSize - 1) / 2 || x == boardSize - offset - 1)
                && (y == offset || y == (boardSize - 1 ) / 2 || y == boardSize - offset - 1))
            return merge(new ArrayList<>(Arrays.asList(grid_c, star)));
        return grid_c;
    }

    private ImageIcon getGridIcon(int x, int y, int boardSize) {
        if (x == 0) {
            if (y == 0)
                return grid_bl;
            else if (y == boardSize - 1)
                return grid_br;
            else
                return grid_bs;
        } else if (x == boardSize - 1) {
            if (y == 0)
                return grid_tl;
            else if (y == boardSize - 1)
                return grid_tr;
            else
                return grid_ts;
        } else {
            if (y == 0)
                return grid_ls;
            else if (y == boardSize - 1)
                return grid_rs;
            else
                return getCenterGridIcon(x, y, boardSize);
        }
    }

    ImageIcon getIcon(StoneColor color, int x, int y, int boardSize) {
        if (color == StoneColor.EMPTY)
            return merge(new ArrayList<>(Arrays.asList(background, getGridIcon(x, y, boardSize))));
        return merge(new ArrayList<>(Arrays.asList(background, getGridIcon(x, y, boardSize), (color == StoneColor.BLACK) ? p1 : p2)));
    }

    int getBoardSize() {
        return BOARD_SIZE;
    }
}
