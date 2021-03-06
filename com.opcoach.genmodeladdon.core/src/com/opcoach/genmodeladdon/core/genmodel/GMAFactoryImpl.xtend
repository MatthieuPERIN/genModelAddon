package com.opcoach.genmodeladdon.core.genmodel

import org.eclipse.emf.codegen.ecore.genmodel.GenClass
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelFactoryImpl

/** This factory overrides the default GenModel Factory, but it is must be setAvailable to use it
 * else, it will create the default GenModel Objects.
 */
class GMAFactoryImpl extends GenModelFactoryImpl {


	override GenFeature createGenFeature() {
		new GMAGenFeatureImpl
	}

	override GenClass createGenClass() {
		new GMAGenClassImpl
	}

	override GenModel createGenModel() {
		new GMAGenModelImpl
	}

	override createGenOperation() {
		new GMAGenOperationImpl
	}

	override createGenPackage() {
		new GMAGenPackageImpl
	}

	override createGenParameter() {
		new GMAGenParameterImpl
	}

	override createGenTypeParameter() {
		new GMAGenTypeParameterImpl
	}

}
