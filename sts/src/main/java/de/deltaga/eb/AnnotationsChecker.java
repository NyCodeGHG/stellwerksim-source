package de.deltaga.eb;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"de.deltaga.eb.EventHandler"})
public class AnnotationsChecker extends AbstractProcessor {
   private Types typeUtils;
   private Elements elementUtils;
   private Filer filer;
   private Messager messager;

   public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      this.typeUtils = processingEnv.getTypeUtils();
      this.elementUtils = processingEnv.getElementUtils();
      this.filer = processingEnv.getFiler();
      this.messager = processingEnv.getMessager();
   }

   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(EventHandler.class)) {
         if (annotatedElement.getKind() != ElementKind.METHOD) {
            this.error(annotatedElement, "Only methods can be annotated with @%s", EventHandler.class.getSimpleName());
         } else {
            ExecutableElement method = (ExecutableElement)annotatedElement;
            if (method.getParameters().size() != 1) {
               this.error(annotatedElement, "Only one parameter allowed with @%s", EventHandler.class.getSimpleName());
            } else {
               this.messager.printMessage(Kind.NOTE, "Found eb");
            }
         }
      }

      return true;
   }

   private void error(Element e, String msg, Object... args) {
      this.messager.printMessage(Kind.ERROR, String.format(msg, args), e);
   }
}
