(ns vdd-core.internal.views
  (:use [hiccup.core]
        [hiccup.page :only [html5 include-css include-js]]))

(def navbar 
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
     [:a {:class "brand" :href "#"} "Project name" ]
     [:div.nav-collapse.collapse
      [:ul.nav
       [:li.active [:a {:href "#"} "Home"]]
       [:li [:a {:href "#about"} "About"]]
       [:li [:a {:href "#contact"} "Contact"]]
       [:li.dropdown
        [:a {:href "#" :class "dropdown-toggle" :data-toggle "dropdown"} "Dropdown" [:b.caret]]
        [:ul.dropdown-menu
         [:li [:a {:href "#"} "Action"]]
         [:li [:a {:href "#"} "Another action"]]
         [:li [:a {:href "#"} "Something else here"]]
         [:li.divider ]
         [:li.nav-header "Nav header" ]
         [:li [:a {:href "#"} "Separated link"]]
         [:li [:a {:href "#"} "One more separated link"]]]]]]]]])

(defn- vdd-page [title & contents]
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
     navbar
     [:div.container contents]
     (include-js "/jquery/jquery.min.js"
                 "/bootstrap/js/bootstrap.min.js"
                 "/autobahn/autobahn.min.js"
                 "/vdd/vdd-core.js")]))

(defn- visualization-link [path title]
  [:p.row 
   [:a.btn.btn-large.span5 {:href path} title]])

(defn find-visualizations [config]
  (let [viz-root (:viz-root config)]
    (->> viz-root
         clojure.java.io/file 
         file-seq 
         (map str)
         (filter #(re-seq #"/index.html$" %))
         (map #(-> (str viz-root "/(.*)/index.html$")
                   re-pattern
                   (re-matches %)
                   last)))))

(defn list-views [config]
  (vdd-page "Visualization Driven Development - Core"
            [:div.hero-unit
              [:h1 "Visualization Driven Development"]
              [:p "TODO description here"]]
            
            [:h3 "Built In Visualizations"]
            (visualization-link "/vdd/data.html" "Data Viewer")
            
            [:h3 (str "Visualizations in /" (:viz-root config))]
            (for [viz (find-visualizations config)]
              (visualization-link (format "/viz/%s" viz ) viz))))
