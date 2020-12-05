/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2020 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.printer;

import java.util.function.Function;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.configuration.ConfigurablePrinter;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

/**
 * Pretty printer for AST nodes.
 */
public class PrettyPrintable implements Printable {
    
    private ConfigurablePrinter configuration;
    
    // visitor factory
    Function<ConfigurablePrinter, VoidVisitor<Void>> visitorFactory;
    
    // static methods 
    
    private static Function<ConfigurablePrinter, VoidVisitor<Void>> createDefaultVisitor() {
        ConfigurablePrinter configuration = createDefaultConfiguration();
        return createDefaultVisitor(configuration);
    }
    
    private static Function<ConfigurablePrinter, VoidVisitor<Void>> createDefaultVisitor(ConfigurablePrinter configuration) {
        return (config) -> new PrintableVisitor(config, new DefaultPrintableSource(config));
    }
    
    private static ConfigurablePrinter createDefaultConfiguration() {
        return new PrinterConfiguration();
    }
    
    // Constructors

    /**
     * Build a new PrettyPrintable with a default configuration and a default factory
     */
    public PrettyPrintable() {
        this(createDefaultVisitor(), createDefaultConfiguration() );
    }
    
    /**
     * Build a new PrettyPrintable with a configuration and a default factory
     * @param configuration
     */
    public PrettyPrintable(ConfigurablePrinter configuration) {
        this(createDefaultVisitor(configuration), configuration );
    }
    
    /**
     * Build a new PrettyPrintable with a configuration and a factory to create a visitor to browse the nodes of the AST
     * @param visitorFactory 
     * @param configuration Configuration to apply
     */
    public PrettyPrintable(Function<ConfigurablePrinter, VoidVisitor<Void>> visitorFactory, ConfigurablePrinter configuration) {
        this.configuration = configuration;
        this.visitorFactory = visitorFactory;
    }
    
    // Methods
    
    /*
     * Returns the Printer configuration
     */
    public ConfigurablePrinter getConfiguration() {
        return configuration;
    }

    /*
     * set or update the PrettyPrinter configuration
     */
    public Printable setConfiguration(ConfigurablePrinter configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public String print(Node node) {
        // lazy initialization of visitor which can have a state (like a buffer)
        VoidVisitor<Void> visitor = visitorFactory.apply(configuration);
        node.accept(visitor, null);
        return visitor.toString();
    }
}
