package core.EDDProject.main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Engine extends SimpleUniverse {
	
	private PickCanvas pickCanvas;//not sure if we need this yet
	
	private Canvas3D canvas;//this is what will display everything
	private BranchGroup group;//the root group
	private TransformGroup transformGroup;//the transform group
	private Container container;//the container we want to contain the canvas in
	private MouseTranslate translateBehaviour;
	private MouseRotate rotateBehaviour;
	private Transform3D transform;
	
	public Engine(Canvas3D canvasIn, Container containerIn){
		
		super(canvasIn);//construct the SimpleUniverse
		canvas = canvasIn;
		container = containerIn;
		container.add(canvas);//add the canvas to the container
		canvas.setSize(new Dimension(600, 400));//set the canvas to 600 by 400
		
		group = new BranchGroup();//create the root group
		group.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		group.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		group.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		group.setCapability(BranchGroup.ALLOW_DETACH);
		
		transformGroup = new TransformGroup();//create the transform group
		//allow read and write for the transform
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		
		rotateBehaviour = new MouseRotate();//create the rotation mouse behaviour
		translateBehaviour = new MouseTranslate();//create the translation mouse behaviour
		
		//give the behaviours the transform group
		rotateBehaviour.setTransformGroup(transformGroup);
		translateBehaviour.setTransformGroup(transformGroup);
		
		//weird stuff that i dont know what it does
		BoundingSphere bounds = new BoundingSphere();
		rotateBehaviour.setSchedulingBounds(bounds);
		translateBehaviour.setSchedulingBounds(bounds);
		
		//set the multipliers for  the rotation and translation
		rotateBehaviour.setFactor(0.05);
		translateBehaviour.setFactor(0.005);
		
		//add the behaviours to the transform group
		transformGroup.addChild(rotateBehaviour);
		transformGroup.addChild(translateBehaviour);
		
		//lazy lights
//		DirectionalLight light = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
//		AmbientLight temp = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
//		temp.setInfluencingBounds(bounds);
//		light.setInfluencingBounds(bounds);
//		group.addChild(light);
//		group.addChild(temp);
		
		//add the transform group to the root group
		group.addChild(transformGroup);
		
		//get  the viewpoint
		this.getViewingPlatform().setNominalViewingTransform();
		
		//add the group to the universe
		this.addBranchGraph(group);
		
		//create the pick canvas
		pickCanvas = new PickCanvas(canvas, group);
		pickCanvas.setMode(PickCanvas.GEOMETRY);
		
		//if our container is a JFrame, set the window to exit on close
		if (container instanceof JFrame){
			((JFrame) container).addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					System.exit(0);
				}
			});
			((JFrame) container).pack();
		}
		
		//set the container to be visible
		container.setVisible(true);
		
	}
	
	public void addShape(float width, float length, float height, Color3f color){//this just makes a rectangle right now
		Material m = new Material();
		m.setEmissiveColor(color);
		m.setLightingEnable(true);
		Appearance app = new  Appearance();
		app.setMaterial(m);
		Box box = new Box(width, length, height, app);
		group.detach();
		transformGroup.addChild(box);
		this.addBranchGraph(group);
		
		
		
	}
	
	public void addShape(float radius, Color3f color){
		Material m = new Material();
		m.setEmissiveColor(color);
		m.setLightingEnable(true);
		Appearance app = new  Appearance();
		app.setMaterial(m);
		Sphere sphere = new Sphere(radius, app);
		group.detach();
		transformGroup.addChild(sphere);
		this.addBranchGraph(group);
	}
	
}
