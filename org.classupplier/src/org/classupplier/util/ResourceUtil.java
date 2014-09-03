package org.classupplier.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.classupplier.Infrastructure;
import org.classupplier.State;
import org.classupplier.impl.ClassSupplierOSGi;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Version;

public class ResourceUtil {

	private static final IPreferencesService preferencesService = Platform
			.getPreferencesService();

	private static String modelFolderName = preferencesService.getString(
			ClassSupplierOSGi.PLUGIN_ID,
			ClassSupplierOSGi.MODEL_FOLDER_PREF_KEY, "model", null);

	private static String fileExt = preferencesService.getString(
			ClassSupplierOSGi.PLUGIN_ID,
			ClassSupplierOSGi.MODEL_RESOURCE_EXT_PREF_KEY, "xmi", null);;

	private static String targetFolderName = preferencesService.getString(
			ClassSupplierOSGi.PLUGIN_ID,
			ClassSupplierOSGi.TARGET_FOLDER_PREF_KEY, "target", null);

	private static final DateFormat qualifierFormat = new SimpleDateFormat(
			"yyyyMMddHHmm");

	static {
		qualifierFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static IPath getModelResourcePath(IProject project,
			Infrastructure workspace) {
		if (workspace == null)
			workspace = ClassSupplierOSGi.getClassSupplier().getWorkspace();
		String name = workspace.getArtifact(project.getName()).getName();
		return project.getFullPath().append(getModelFolderName())
				.append(getFileName(name));
	}

	public static IPath getTargetResourcePath(IProject project, State state) {
		return project.getLocation().append(getTargetFolderName())
				.append(getJarName(state)).addFileExtension("jar");
	}

	/**
	 * Returns the name of the folder within project, where the model resource
	 * is located.
	 * 
	 * @return
	 */
	public static String getModelFolderName() {
		return modelFolderName;
	}

	public static String getTargetFolderName() {
		return targetFolderName;
	}

	public static String getModelFileExt() {
		return fileExt;
	}

	public static String getFileName(String name) {
		return name + '.' + getModelFileExt();
	}

	public static IPath getDefaultExportDestination() {
		return getWorkspace().removeLastSegments(1);
	}

	public static IPath getWorkspace() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}

	public static IProject getProject(String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	public static IProjectDescription addProjectNature(
			IProjectDescription description, String natureId) {
		String[] oldNatures = description.getNatureIds();
		String[] newNatures = new String[oldNatures.length + 1];
		System.arraycopy(oldNatures, 0, newNatures, 0, oldNatures.length);
		newNatures[oldNatures.length] = natureId;
		description.setNatureIds(newNatures);
		return description;
	}

	public static void setAutoBuilding(IWorkspace workspace, boolean value)
			throws CoreException {
		IWorkspaceDescription d = workspace.getDescription();
		d.setAutoBuilding(value);
		workspace.setDescription(d);
	}

	public static String getJarName(State state) {
		String jarName;
		Version version = state.getVersion();
		if (version.equals(Version.emptyVersion))
			jarName = state.getProjectName() + '-'
					+ Version.parseVersion("1.0.0.qualifier").toString();
		else
			jarName = state.getProjectName() + '-' + version;
		return jarName;
	}

	public static String formatQualifier(Date timestamp) {
		return qualifierFormat.format(timestamp);
	}

	public static URI workspaceResourceURI(String fileName) {
		return URI.createURI("platform:/meta/" + ClassSupplierOSGi.PLUGIN_ID
				+ "/" + fileName);
	}

	public static String workspaceResourcePath(String fileName) {
		return ClassSupplierOSGi.getInstance().getStateLocation()
				.append(fileName).toString();
	}

}