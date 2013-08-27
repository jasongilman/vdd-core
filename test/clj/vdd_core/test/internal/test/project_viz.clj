(ns vdd-core.test.internal.test.project-viz
  (:require [clojure.test :refer :all]
            [vdd-core.internal.project-viz :as pv]
            [me.raynes.fs :as fs]
            [clojure.string]))

(def project-viz-fixtures
  [{:title "viz1_clj"
    :driver "driver.clj"}
   {:title "viz2_clj"
    :driver "driver.clj"}
   {:title "viz3_rb"
    :driver "driver.rb"}
   {:title "viz4"
    :driver nil}])

(def viz-root (atom nil))

(def expected-project-vizs (atom nil))

(defn path-join 
  "Joins the path names given by the path separator."
  [& args]
  (clojure.string/join java.io.File/separator args))

(defn create-project-viz-files 
  "Creates a set of temporary folders on the filesystem representing a set of visualizations that the
  project-visualizations function should find."
  []
  (let [viz-dir (fs/temp-dir "viz-root")]
    (reset! viz-root viz-dir)
    (reset! expected-project-vizs
            (doall (for [{title :title driver :driver} project-viz-fixtures]
                     (let [viz-path (path-join viz-dir title)
                           driver-path (if driver (path-join viz-path driver) nil)]
                       (fs/mkdir viz-path)
                       (fs/touch (path-join viz-path "index.html"))
                       (when driver-path (fs/touch driver-path))
                       {:path viz-path
                        :title title
                        :driver driver-path}))))
    ; Create an extra folder where no visualization should be found
    (let [viz-ignore-path (path-join viz-dir "viz-ignore")]
      (fs/mkdir viz-ignore-path)
      (fs/touch (path-join viz-ignore-path "foo.html")))))

(defn cleanup-project-viz-files 
  "Cleans up the temporary visualization folder."
  []
  (when @viz-root
    (fs/delete-dir @viz-root)
    (reset! viz-root nil)
    (reset! expected-project-vizs nil)))

(use-fixtures :once 
  (fn [f]
    (create-project-viz-files)
    (f)
    (cleanup-project-viz-files)))


(deftest test-project-visualizations
  (is (= @expected-project-vizs 
         (pv/project-visualizations {:viz-root @viz-root}))))


