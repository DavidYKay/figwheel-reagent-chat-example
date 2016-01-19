(ns chat.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:text "Hello world!"
                            :chat-input ""
                            }))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn main-ui []
  [:div

   [:div "App state:"]
   [:div (:text @app-state)]

   [:div "Chat Input:"]
   [:div (:chat-input @app-state)]

   [:input
    {:placeholder "My message here...",
     :type "text"
     :value (:chat-input @app-state)
     :on-change (fn [ev]
                  (swap! app-state assoc :chat-input (-> ev .-target .-value)))}]

   ])

(r/render-component [main-ui]
                    (js/document.getElementById "app"))
