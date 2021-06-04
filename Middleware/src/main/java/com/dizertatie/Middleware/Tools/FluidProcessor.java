package com.dizertatie.Middleware.Tools;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("FluidIO")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class FluidProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> arg0, RoundEnvironment arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
