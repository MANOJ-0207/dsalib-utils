// ============================
// THEME MANAGEMENT MODULE
// ============================
const ThemeManager = {
    /**
     * Applies the saved theme or default theme based on user preference
     */
    applySavedTheme() {
        const savedTheme = localStorage.getItem("theme");
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        const theme = savedTheme || (prefersDark ? "dark" : "light");
        document.documentElement.setAttribute("data-theme", theme);
    },

    /**
     * Toggles between light and dark theme
     */
    toggleTheme() {
        const root = document.documentElement;
        const currentTheme = root.getAttribute("data-theme");
        const newTheme = currentTheme === "dark" ? "light" : "dark";
        root.setAttribute("data-theme", newTheme);
        localStorage.setItem("theme", newTheme);
    },

    /**
     * Initializes theme management
     */
    init() {
        this.applySavedTheme();
        const themeToggle = document.getElementById("themeToggle");
        if (themeToggle) {
            themeToggle.addEventListener("click", () => this.toggleTheme());
        }
    }
};

// ============================
// ACCORDION ANIMATION MODULE
// ============================
const AccordionAnimator = {
    /**
     * Expands an accordion panel with smooth animation
     * @param {HTMLElement} panel - The panel to expand
     * @param {HTMLElement} chevron - The chevron element to rotate
     */
    expand(panel, chevron) {
        panel.classList.add('opening');
        panel.style.height = '0px';
        panel.style.visibility = 'visible';
        panel.style.pointerEvents = 'auto';

        // Get natural height
        const height = panel.scrollHeight + 'px';

        requestAnimationFrame(() => {
            panel.style.height = height;
        });

        panel.addEventListener('transitionend', function onEnd(e) {
            if (e.propertyName === 'height') {
                panel.classList.remove('opening');
                panel.classList.add('open');
                panel.style.height = 'auto'; // Remove fixed height
                panel.removeEventListener('transitionend', onEnd);
                
                // Ensure all child accordions are closed when parent opens
                AccordionAnimator.ensureChildAccordionsClosed(panel);
            }
        });

        if (chevron) chevron.textContent = '▾';
    },

    /**
     * Collapses an accordion panel with smooth animation
     * @param {HTMLElement} panel - The panel to collapse
     * @param {HTMLElement} chevron - The chevron element to rotate
     */
    collapse(panel, chevron) {
        // Close all child accordions first
        this.closeAllChildAccordions(panel);
        
        panel.classList.add('closing');

        // Set fixed height before collapsing
        panel.style.height = panel.scrollHeight + 'px';

        requestAnimationFrame(() => {
            panel.style.height = '0px';
        });

        panel.addEventListener('transitionend', function onEnd(e) {
            if (e.propertyName === 'height') {
                panel.classList.remove('closing', 'open');
                panel.style.height = '';
                panel.style.visibility = 'hidden';
                panel.style.pointerEvents = 'none';
                panel.removeEventListener('transitionend', onEnd);
            }
        });

        if (chevron) chevron.textContent = '▸';
    },

    /**
     * Closes all child accordion panels within a parent panel
     * @param {HTMLElement} parentPanel - The parent panel to search for child accordions
     */
    closeAllChildAccordions(parentPanel) {
        const childPanels = parentPanel.querySelectorAll('.accordion-panel.open');
        childPanels.forEach(childPanel => {
            const parentButton = childPanel.previousElementSibling;
            if (parentButton && parentButton.classList.contains('accordion-btn')) {
                const childChevron = parentButton.querySelector('.chevron');
                
                // Directly close without animation for cleaner UX
                childPanel.classList.remove('open', 'opening', 'closing');
                childPanel.style.height = '';
                childPanel.style.visibility = 'hidden';
                childPanel.style.pointerEvents = 'none';
                
                if (childChevron) childChevron.textContent = '▸';
            }
        });
    },

    /**
     * Ensures all child accordions are closed when parent opens
     * @param {HTMLElement} parentPanel - The parent panel that was opened
     */
    ensureChildAccordionsClosed(parentPanel) {
        // Wait for the parent to finish opening, then ensure children are closed
        setTimeout(() => {
            this.closeAllChildAccordions(parentPanel);
        }, 50); // Small delay to ensure parent animation completes
    },

    /**
     * Toggle accordion panel open/closed state
     * @param {HTMLElement} button - The accordion button that was clicked
     */
    toggleAccordion(button) {
        const panel = button.nextElementSibling;
        const chevron = button.querySelector('.chevron');

        if (panel.classList.contains('open')) {
            this.collapse(panel, chevron);
        } else {
            this.expand(panel, chevron);
        }
    }
};

// ============================
// DOCUMENTATION DATA
// ============================
const DOC_DATA = {
    cache: {
        type: 'package',
        items: [
            { name: 'Cache', type: 'interface', path: 'docs/cache/Cache.html' },
            { name: 'AbstractCache', type: 'abstract', path: 'docs/cache/AbstractCache.html' },
            { name: 'FIFOCache', type: 'class', path: 'docs/cache/FIFOCache.html' },
            { name: 'LFUCache', type: 'class', path: 'docs/cache/LFUCache.html' },
            { name: 'LRUCache', type: 'class', path: 'docs/cache/LRUCache.html' },
            { name: 'SegmentedLRUCache', type: 'class', path: 'docs/cache/SegmentedLRUCache.html' }
        ]
    },
    graph: {
        type: 'package',
        items: {
            disjointset: {
                type: 'package',
                items: [
                    { name: 'DisjointSet', type: 'class', path: 'docs/graph/disjointset/DisjointSet.html' }
                ]
            },
            unweightedgraph: {
                type: 'package',
                items: [
                    { name: 'SimpleGraph', type: 'interface', path: 'docs/graph/unweightedgraph/SimpleGraph.html' },
                    { name: 'Edge', type: 'record', path: 'docs/graph/unweightedgraph/Edge.html' },
                    { name: 'AdjListSimpleGraph', type: 'class', path: 'docs/graph/unweightedgraph/AdjListSimpleGraph.html' },
                    { name: 'AdjMatrixSimpleGraph', type: 'class', path: 'docs/graph/unweightedgraph/AdjMatrixSimpleGraph.html' }
                ]
            },
            weightedgraph: {
                type: 'package',
                items: {
                    edge: {
                        type: 'package',
                        items: [
                            { name: 'DetailedEdge', type: 'record', path: 'docs/graph/weightedgraph/edge/DetailedEdge.html' },
                            { name: 'WeightedEdge', type: 'record', path: 'docs/graph/weightedgraph/edge/WeightedEdge.html' }
                        ]
                    },
                    WeightedGraph: { name: 'WeightedGraph', type: 'interface', path: 'docs/graph/weightedgraph/WeightedGraph.html' },
                    AdjListWeightedGraph: { name: 'AdjListWeightedGraph', type: 'class', path: 'docs/graph/weightedgraph/AdjListWeightedGraph.html' },
                    AdjMatrixWeightedGraph: { name: 'AdjMatrixWeightedGraph', type: 'class', path: 'docs/graph/weightedgraph/AdjMatrixWeightedGraph.html' }
                }
            }
        }
    },
    heap: {
        type: 'package',
        items: [
            { name: 'EfficientHeap', type: 'class', path: 'docs/heap/EfficientHeap.html' }
        ]
    },
    math: {
        type: 'package',
        items: {
            combinatorics: {
                type: 'package',
                items: [
                    { name: 'FactorialUtils', type: 'class', path: 'docs/math/combinatorics/FactorialUtils.html' },
                    { name: 'PascalTriangleUtils', type: 'class', path: 'docs/math/combinatorics/PascalTriangleUtils.html' }
                ]
            },
            numbertheory: {
                type: 'package',
                items: [
                    { name: 'ModMath', type: 'class', path: 'docs/math/numbertheory/ModMath.html' },
                    { name: 'NumberTheoryUtils', type: 'class', path: 'docs/math/numbertheory/NumberTheoryUtils.html' },
                    { name: 'PrimeUtils', type: 'class', path: 'docs/math/numbertheory/PrimeUtils.html' }
                ]
            }
        }
    },
    rangequery: {
        type: 'package',
        items: {
            segmenttree: {
                type: 'package',
                items: [
                    { name: 'SegmentTree', type: 'interface', path: 'docs/rangequery/segmenttree/SegmentTree.html' },
                    { name: 'GenericEagerSegmentTree', type: 'class', path: 'docs/rangequery/segmenttree/GenericEagerSegmentTree.html' },
                    { name: 'GenericLazySegmentTree', type: 'class', path: 'docs/rangequery/segmenttree/GenericLazySegmentTree.html' }
                ]
            },
            Generic2DSparseTable: { name: 'Generic2DSparseTable', type: 'class', path: 'docs/rangequery/Generic2DSparseTable.html' },
            GenericSparseTable: { name: 'GenericSparseTable', type: 'class', path: 'docs/rangequery/GenericSparseTable.html' },
            OrderStatisticSet: { name: 'OrderStatisticSet', type: 'class', path: 'docs/rangequery/OrderStatisticSet.html' },
            OrderStatisticTree: { name: 'OrderStatisticTree', type: 'class', path: 'docs/rangequery/OrderStatisticTree.html' }
        }
    },
    stringutils: {
        type: 'package',
        items: {
            pattern: {
                type: 'package',
                items: [
                    { name: 'PatternMatcher', type: 'interface', path: 'docs/stringutils/pattern/PatternMatcher.html' },
                    { name: 'AbstractPatternMatcher', type: 'abstract', path: 'docs/stringutils/pattern/AbstractPatternMatcher.html' },
                    { name: 'KMPMatcher', type: 'class', path: 'docs/stringutils/pattern/KMPMatcher.html' },
                    { name: 'RabinKarpMatcher', type: 'class', path: 'docs/stringutils/pattern/RabinKarpMatcher.html' },
                    { name: 'ZAlgorithmMatcher', type: 'class', path: 'docs/stringutils/pattern/ZAlgorithmMatcher.html' }
                ]
            },
            Trie: { name: 'Trie', type: 'class', path: 'docs/stringutils/Trie.html' },
            TrieNode: { name: 'TrieNode', type: 'class', path: 'docs/stringutils/TrieNode.html' }
        }
    },
    tree: {
        type: 'package',
        items: [
            { name: 'GenericTree', type: 'class', path: 'docs/tree/GenericTree.html' }
        ]
    }
};

// ============================
// SVG ICON FACTORY
// ============================
const SVGIconFactory = {
    /**
     * Creates SVG namespace element
     * @param {string} tagName - SVG element tag name
     * @param {Object} attributes - Attributes to set on the element
     * @returns {SVGElement} The created SVG element
     */
    createSVGElement(tagName, attributes = {}) {
        const element = document.createElementNS('http://www.w3.org/2000/svg', tagName);
        Object.entries(attributes).forEach(([key, value]) => {
            element.setAttribute(key, value);
        });
        return element;
    },

    /**
     * Creates an interface icon SVG
     * @returns {SVGElement} Interface icon
     */
    createInterfaceIcon() {
        const svg = this.createSVGElement('svg', {
            class: 'svg',
            width: '20',
            height: '20',
            viewBox: '0 0 40 40'
        });

        const circle = this.createSVGElement('circle', {
            cx: '20',
            cy: '20',
            r: '18',
            stroke: 'currentColor',
            'stroke-width': '2',
            fill: 'none'
        });

        const group = this.createSVGElement('g', { fill: 'currentColor' });
        
        const rect1 = this.createSVGElement('rect', {
            x: '17', y: '10', width: '6', height: '2'
        });
        
        const rect2 = this.createSVGElement('rect', {
            x: '19', y: '12', width: '2', height: '12'
        });
        
        const rect3 = this.createSVGElement('rect', {
            x: '17', y: '24', width: '6', height: '2'
        });

        group.appendChild(rect1);
        group.appendChild(rect2);
        group.appendChild(rect3);

        svg.appendChild(circle);
        svg.appendChild(group);

        return svg;
    },

    /**
     * Creates a class icon SVG
     * @returns {SVGElement} Class icon
     */
    createClassIcon() {
        const svg = this.createSVGElement('svg', {
            class: 'svg',
            width: '20',
            height: '20',
            viewBox: '0 0 40 40'
        });

        const circle = this.createSVGElement('circle', {
            cx: '20',
            cy: '20',
            r: '18',
            stroke: 'currentColor',
            'stroke-width': '2',
            fill: 'none'
        });

        const text = this.createSVGElement('text', {
            x: '20',
            y: '27',
            'text-anchor': 'middle',
            'font-size': '20',
            'font-family': 'Arial, sans-serif',
            'font-weight': 'bold',
            fill: 'currentColor'
        });
        text.textContent = 'C';

        svg.appendChild(circle);
        svg.appendChild(text);

        return svg;
    },

    /**
     * Creates an abstract class icon SVG
     * @returns {SVGElement} Abstract class icon
     */
    createAbstractIcon() {
        const svg = this.createSVGElement('svg', {
            class: 'svg',
            width: '20',
            height: '20',
            viewBox: '0 0 40 40'
        });

        const circle = this.createSVGElement('circle', {
            cx: '20',
            cy: '20',
            r: '18',
            stroke: 'currentColor',
            'stroke-width': '2',
            fill: 'none'
        });

        const text = this.createSVGElement('text', {
            x: '20',
            y: '27',
            'text-anchor': 'middle',
            'font-size': '20',
            'font-family': 'Arial, sans-serif',
            'font-weight': 'bold',
            fill: 'currentColor'
        });
        text.textContent = 'A';

        svg.appendChild(circle);
        svg.appendChild(text);

        return svg;
    },

    /**
     * Creates a record icon SVG
     * @returns {SVGElement} Record icon
     */
    createRecordIcon() {
        const svg = this.createSVGElement('svg', {
            class: 'svg',
            width: '20',
            height: '20',
            viewBox: '0 0 40 40'
        });

        const circle = this.createSVGElement('circle', {
            cx: '20',
            cy: '20',
            r: '18',
            stroke: 'currentColor',
            'stroke-width': '2',
            fill: 'none'
        });

        const text = this.createSVGElement('text', {
            x: '20',
            y: '27',
            'text-anchor': 'middle',
            'font-size': '20',
            'font-family': 'Arial, sans-serif',
            'font-weight': 'bold',
            fill: 'currentColor'
        });
        text.textContent = 'R';

        svg.appendChild(circle);
        svg.appendChild(text);

        return svg;
    },

    /**
     * Gets the appropriate icon for a given type
     * @param {string} type - The type (interface, class, abstract, record)
     * @returns {SVGElement} The icon element
     */
    getIcon(type) {
        switch (type) {
            case 'interface':
                return this.createInterfaceIcon();
            case 'abstract':
                return this.createAbstractIcon();
            case 'record':
                return this.createRecordIcon();
            case 'class':
            default:
                return this.createClassIcon();
        }
    }
};

// ============================
// DOM ELEMENT FACTORY
// ============================
const DOMElementFactory = {
    /**
     * Creates a chevron span element
     * @param {string} direction - Direction of chevron ('▸' or '▾')
     * @returns {HTMLSpanElement} Chevron span element
     */
    createChevron(direction = '▸') {
        const chevron = document.createElement('span');
        chevron.className = 'chevron';
        chevron.textContent = direction;
        return chevron;
    },

    /**
     * Creates a package name strong element
     * @param {string} packageName - Name of the package
     * @returns {HTMLElement} Strong element with package name
     */
    createPackageName(packageName) {
        const strong = document.createElement('strong');
        strong.textContent = `📦 ${packageName}`;
        return strong;
    },

    /**
     * Creates an accordion button
     * @param {string} packageName - Name of the package
     * @param {Function} clickHandler - Click event handler
     * @returns {HTMLButtonElement} Accordion button element
     */
    createAccordionButton(packageName, clickHandler) {
        const button = document.createElement('button');
        button.className = 'accordion-btn';
        button.addEventListener('click', clickHandler);
        
        const chevron = this.createChevron();
        const packageNameElement = this.createPackageName(packageName);
        
        button.appendChild(chevron);
        button.appendChild(document.createTextNode(' '));
        button.appendChild(packageNameElement);
        
        return button;
    },

    /**
     * Creates an accordion panel
     * @returns {HTMLDivElement} Accordion panel element
     */
    createAccordionPanel() {
        const panel = document.createElement('div');
        panel.className = 'accordion-panel';
        return panel;
    },

    /**
     * Creates a documentation link
     * @param {Object} item - The document item with name, type, and path
     * @returns {HTMLAnchorElement} Documentation link element
     */
    createDocumentationLink(item) {
        const link = document.createElement('a');
        link.href = item.path;
        link.className = `doc-link ${item.type}-link`;
        link.target = '_blank';
        
        const icon = SVGIconFactory.getIcon(item.type);
        const textNode = document.createTextNode(item.name);
        
        link.appendChild(icon);
        link.appendChild(document.createTextNode(' '));
        link.appendChild(textNode);
        
        return link;
    },

    /**
     * Creates a main heading
     * @param {string} text - Heading text
     * @returns {HTMLHeadingElement} H3 heading element
     */
    createHeading(text) {
        const heading = document.createElement('h3');
        heading.textContent = text;
        return heading;
    },

    /**
     * Creates an accordion section wrapper
     * @returns {HTMLElement} Section element for accordion
     */
    createAccordionSection() {
        const section = document.createElement('section');
        section.className = 'accordion';
        return section;
    }
};

// ============================
// ACCORDION NAVIGATION MODULE
// ============================
const AccordionNavigation = {
    /**
     * Creates accordion structure from data object
     * @param {Object} data - The documentation data structure
     * @param {HTMLElement} container - The container element to append to
     */
    createAccordion(data, container) {
        Object.keys(data).forEach(key => {
            const item = data[key];
            
            if (item.type === 'package') {
                this.createPackageSection(key, item, container);
            } else if (item.name) {
                // Direct item (not in array)
                const link = DOMElementFactory.createDocumentationLink(item);
                container.appendChild(link);
            }
        });
    },

    /**
     * Creates a package section with button and panel
     * @param {string} packageName - Name of the package
     * @param {Object} packageData - Package data object
     * @param {HTMLElement} container - Container to append to
     */
    createPackageSection(packageName, packageData, container) {
        // Create package button with click handler
        const button = DOMElementFactory.createAccordionButton(
            packageName, 
            () => AccordionAnimator.toggleAccordion(button)
        );
        
        // Create panel
        const panel = DOMElementFactory.createAccordionPanel();
        
        // Process items
        this.processPackageItems(packageData.items, panel);
        
        container.appendChild(button);
        container.appendChild(panel);
    },

    /**
     * Processes package items (arrays or nested objects)
     * @param {Array|Object} items - Items to process
     * @param {HTMLElement} panel - Panel to append items to
     */
    processPackageItems(items, panel) {
        if (Array.isArray(items)) {
            // Array of direct items
            items.forEach(docItem => {
                const link = DOMElementFactory.createDocumentationLink(docItem);
                panel.appendChild(link);
            });
        } else {
            // Nested structure - recursively create accordion
            this.createAccordion(items, panel);
        }
    },

    /**
     * Initializes the accordion navigation and populates the container
     */
    init() {
        const container = document.getElementById('container');
        if (!container) {
            console.error('Container element with ID "container" not found');
            return;
        }

        // Clear any existing content
        container.innerHTML = '';

        // Create and append heading
        const heading = DOMElementFactory.createHeading('Package Structure');
        container.appendChild(heading);

        // Create accordion wrapper
        const accordionElement = DOMElementFactory.createAccordionSection();
        
        // Populate the accordion with data
        this.createAccordion(DOC_DATA, accordionElement);
        
        // Append to container
        container.appendChild(accordionElement);

        console.log('Documentation navigation populated successfully');
    }
};

// ============================
// APPLICATION INITIALIZATION
// ============================
const App = {
    /**
     * Initializes the entire application
     */
    init() {
        console.log('Initializing application...');
        ThemeManager.init();
        AccordionNavigation.init();
        console.log('Application initialized successfully');
    }
};

// ============================
// DOM READY EVENT LISTENER
// ============================
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});