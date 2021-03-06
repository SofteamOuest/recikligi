package net.ludeo.recikligi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class VisualClassTranslation extends AbstractEntityWithId {

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visual_class_id")
    private VisualClass visualClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translation_locale_id")
    private TranslationLocale translationLocale;
}
