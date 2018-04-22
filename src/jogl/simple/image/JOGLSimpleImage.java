/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogl.simple.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 *
 * @author russell3233
 */
public class JOGLSimpleImage extends JPanel{

// <editor-fold desc="/*********************************** Variables ********************************/">

    Images images;
    BufferedImage initials, house, sun;
    
    static int translateX = 0;
    static int translateY = 0;
    static double rotation = 0.0;
    static double scaleX = 1.0;
    static double scaleY = 1.0;
    
    int frameCount;
    long elapsedTime;
    
    private float pixelSize;
    
// </editor-fold>    
    
    
    JOGLSimpleImage () {
        images = new Images();
        initials = images.getImage(Images.initials);
        house = images.getImage(Images.house);
        sun = images.getImage(Images.sun);
    }

    public static void main(String[] args) {
        
// <editor-fold desc="/*********************************** Main Menu ********************************/">
        JFrame mainFrame;
        
        Images images = new Images();
        
        // Set up title and mainframe
        mainFrame = new JFrame("Lilljedahl CMSC 405 Project 1");
        mainFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // refer to window listener
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null); // Center the frame on the screen    
        mainFrame.setResizable(false);
        
        JOGLSimpleImage mainPanel = new JOGLSimpleImage();
        mainFrame.setContentPane(mainPanel);
        
        // build Window Listener for exit button
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int choice = JOptionPane.showConfirmDialog(mainFrame, 
                    "Are you sure to close this window?", "Really Closing?", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);  
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }          
            }
        }); // end Window listener    

        Timer animationTimer;  // A Timer that will emit events to drive the animation.
        final long startTime = System.currentTimeMillis();
        // Taken from AnimationStarter
        // Modified to change timing and allow for recycling
        animationTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (mainPanel.frameCount > 5) {
                    mainPanel.frameCount = 0;
                } else {
                    mainPanel.frameCount++;
                }
                mainPanel.elapsedTime = System.currentTimeMillis() - startTime;
                mainPanel.repaint();
            }
        });
        
        mainFrame.setVisible(true);
        animationTimer.start();
        
    } // end Main gui
// </editor-fold>
        
// <editor-fold desc="/*********************************** Paint ********************************/">
    public void paint(Graphics graphics) {
        
        Graphics2D graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setPaint(Color.BLACK);
        graphics2d.fillRect(0, 0, getWidth(), getHeight());
        
        // used to zoom in to images
        applyWindowToViewportTransformation(graphics2d, -75, 75, -75, 75, true);
        
        AffineTransform savedTransform = graphics2d.getTransform();
        
        // frame by frame to show movement
        // must be displayed in sequence and always start from the transformed
        // image, not the original
        System.out.println("Frame: " + frameCount);
        switch (frameCount) {
            case 1: 
                translateX = 0;
                translateY = 0;
                scaleX = 1.0;
                scaleY = 1.0;
                rotation = 0;
                break;
            case 2: // x - 5, y + 7
                translateX = -5;
                translateY = 7;
                break;
            case 3: // rotate 45 degrees counter clockwise
                rotation = 45 * Math.PI / 180;
                break;
            case 4: // rotate 90 degrees clockwise
                rotation = 90 * Math.PI;
                break;
            case 5: // scale 2 times x, .5 times y
                scaleX = 2.0;
                scaleY = 0.5;
                break;
            default:
                break;
        } // end frame switch
        
        // draw and transform initials
        graphics2d.translate(translateX, translateY);
        graphics2d.translate(-10,-40);
        graphics2d.rotate(rotation);
        graphics2d.scale(scaleX, scaleY);
        System.out.println("Adding initials");
        graphics2d.drawImage(initials, 0, 0, this);
        graphics2d.setTransform(savedTransform);

        // draw and transform house
        graphics2d.translate(translateX, translateY);
        graphics2d.translate(0, 0);
        graphics2d.rotate(rotation);
        graphics2d.scale(scaleX, scaleY);
        System.out.println("Adding house");
        graphics2d.drawImage(house, 0, 0, this);
        graphics2d.setTransform(savedTransform);        
        
        // draw and transform sun
        graphics2d.translate(translateX, translateY);
        graphics2d.translate(-40,40);
        graphics2d.rotate(rotation);
        graphics2d.scale(scaleX, scaleY);
        System.out.println("Adding sun");
        graphics2d.drawImage(sun, 0, 0, this);
        graphics2d.setTransform(savedTransform);        
        
    } // end paint
// </editor-fold>
    
    // Method taken directly from AnimationStarter.java Code
    private void applyWindowToViewportTransformation(Graphics2D g2,
            double left, double right, double bottom, double top,
            boolean preserveAspect) {
        int width = getWidth();   // The width of this drawing area, in pixels.
        int height = getHeight(); // The height of this drawing area, in pixels.
        if (preserveAspect) {
            // Adjust the limits to match the aspect ratio of the drawing area.
            double displayAspect = Math.abs((double) height / width);
            double requestedAspect = Math.abs((bottom - top) / (right - left));
            if (displayAspect > requestedAspect) {
                // Expand the viewport vertically.
                double excess = (bottom - top) * (displayAspect / requestedAspect - 1);
                bottom += excess / 2;
                top -= excess / 2;
            } else if (displayAspect < requestedAspect) {
                // Expand the viewport vertically.
                double excess = (right - left) * (requestedAspect / displayAspect - 1);
                right += excess / 2;
                left -= excess / 2;
            }
        }
        g2.scale(width / (right - left), height / (bottom - top));
        g2.translate(-left, -top);
        double pixelWidth = Math.abs((right - left) / width);
        double pixelHeight = Math.abs((bottom - top) / height);
        pixelSize = (float) Math.max(pixelWidth, pixelHeight);
    }    
}
