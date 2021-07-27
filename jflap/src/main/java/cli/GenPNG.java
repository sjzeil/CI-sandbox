/**
 * 
 */
package cli;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import javax.imageio.ImageIO;

import automata.Automaton;
import automata.AutomatonSimulator;
import automata.SimulatorFactory;
import automata.fsa.FiniteStateAutomaton;
import automata.graph.FSAEqualityChecker;
import automata.turing.TMSimulator;
import automata.turing.Tape;
import automata.turing.TuringMachine;
import file.XMLCodec;
import gui.viewer.AutomatonDrawer;
import regular.RegularExpression;

/**
 * @author zeil
 *
 */
public class GenPNG {

    /**
     * Usage:
     *   java [options] cli.GenPNG jffFile pngFile
     *   
     * Generates a PNG picture of an automaton
     * 
     * @param args command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 2) {
            System.err.println(
                    "Usage:\n" +
                            "   java [options] cli.GenPNG jffFile pngFile");
            System.exit(-1);
        }
        new GenPNG().runProgram(args);
    }

    public void runProgram(String[] args) throws IOException {
        CLI.launchedByCLI = true;
        File jffFile = new File(args[0]);
        File pngFile = new File(args[1]);

        Automaton automaton
            = (Automaton)new XMLCodec().decode(jffFile, null);
        AutomatonDrawer drawer = new AutomatonDrawer(automaton);
        Rectangle bounds = drawer.getBounds();
        System.out.println("Bounds: " + bounds);
        
        int margin = 10;
        int width = bounds.width + 2*margin;
        int height = bounds.height + 2*margin;
        BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimg.createGraphics();
        g.setColor(Color.white);

        g.fillRect(0,  0,  width, height);
        
        g.translate(margin-bounds.x, margin-bounds.y);
        
        drawer.drawAutomaton(g);
        
        ImageIO.write(bimg, "PNG", pngFile);
        
    }   

}
