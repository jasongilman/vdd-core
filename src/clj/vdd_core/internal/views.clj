(ns vdd-core.internal.views
  (:use [hiccup.core]
        [hiccup.page :only [html5 include-css include-js]]))

(defn- navbar 
  "Creates a navbar for vdd page. Takes a list of visualization tuples to link to which
  should contain a path and title."
  [vizs]
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.navbar-inner
    [:div.container
     [:button {:type "button" 
               :class "btn btn-navbar" 
               :data-toggle "collapse" 
               :data-target ".nav-collapse"}
      [:span.icon-bar ]
      [:span.icon-bar ]
      [:span.icon-bar ]]
     [:a {:class "brand" :href "/"} "VDD" ]
     [:div.nav-collapse.collapse
      [:ul.nav
       [:li.active [:a {:href "/"} "Home"]]
       [:li [:a {:href "http://visualizationdrivendevelopment.com"} "About"]]
       [:li.dropdown
        [:a {:href "#" :class "dropdown-toggle" :data-toggle "dropdown"} "Visualizations" [:b.caret]]
        [:ul.dropdown-menu
         [:li.nav-header "Built in" ]
         (for [{path :path title :title} (:built-in vizs)]
           [:li [:a {:href path} title]])
         [:li.divider ]
         [:li.nav-header "Project" ]
         (for [{path :path title :title} (:project vizs)]
           [:li [:a {:href path} title]])]]]]]]])

(defn- vdd-page 
  "Creates a visualization page taking the page title, the vizs to link to in the
  navbar and the page contents."
  [title vizs & contents]
  (html5 
    [:head 
     [:meta {:charset "utf-8"}]
     [:title title]
     [:meta {:name "viewport" 
             :content "width=device-width, initial-scale=1.0"}]
     (include-css "/bootstrap/css/bootstrap.min.css" 
                  "/bootstrap/css/bootstrap-responsive.min.css" 
                  "/vdd/vdd.css")]
    [:body 
     (navbar vizs)
     [:div.container contents]
     (include-js "/jquery/jquery.min.js"
                 "/bootstrap/js/bootstrap.min.js"
                 "/autobahn/autobahn.min.js"
                 "/vdd/vdd-core.js")]))

(defn find-visualizations-in-viz-root
  "Finds the visualizations available in the configured :viz-root."
  [config]
  (let [viz-root (:viz-root config)]
    (->> viz-root
         clojure.java.io/file 
         file-seq 
         (map str)
         (filter #(re-seq #"/index.html$" %))
         (map #(-> (str viz-root "/(.*)/index.html$")
                   re-pattern
                   (re-matches %)
                   last))
         (map (fn [viz] {:path (format "/viz/%s" viz) 
                      :title viz})))))

(defn visualizations [config]
  {:built-in [{:path "/vdd/data.html" :title "Data Viewer"}]
   :project (find-visualizations-in-viz-root config)})

(defn list-views-page
  "Returns a page containing the visualizations available"
  [config]
  (let [vizs (visualizations config)
        viz-link-fn (fn [{path :path title :title}]
                      [:p.row 
                       [:a.btn.btn-large.span5 {:href path} title]])]
    (vdd-page 
      "Visualization Driven Development - Core"
      vizs
      [:div.hero-unit
       [:h1 "Visualization Driven Development"]
       [:p "TODO description here"]]
      
      [:h3 "Built In Visualizations"]
      (for [viz (:built-in vizs)]
        (viz-link-fn viz))
      
      [:h3 "Project Visualizations"]
      (for [viz (:project vizs)]
        (viz-link-fn viz)))))
