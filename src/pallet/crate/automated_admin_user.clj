(ns pallet.crate.automated-admin-user
  (:require
   [pallet.action.user :as user]
   [pallet.context :as context]
   [pallet.crate.ssh-key :as ssh-key]
   [pallet.utils :as utils]
   [pallet.session :as session]
   [pallet.thread-expr :as thread-expr]
   [pallet.crate.sudoers :as sudoers])
  (:use
   [pallet.phase :only [def-crate-fn]]))

(def-crate-fn authorize-user-key
  [username path-or-bytes]
  (if (string? path-or-bytes)
    (ssh-key/authorize-key username (slurp path-or-bytes))
    (ssh-key/authorize-key username (String. path-or-bytes))))

(def-crate-fn automated-admin-user
  "Builds a user for use in remote-admin automation. The user is given
  permission to sudo without password, so that passwords don't have to appear
  in scripts, etc."
  ([]
     [user session/admin-user
      user (m-result (or user utils/*admin-user*))]
       (automated-admin-user (:username user) (:public-key-path user)))
  ([username]
     [user session/admin-user
      user (m-result (or user utils/*admin-user*))]
       (automated-admin-user username (:public-key-path user)))
  ([username & public-key-paths]
     (sudoers/install)
     (user/user username :create-home true :shell :bash)
     (map (partial authorize-user-key username) public-key-paths)
     (sudoers/sudoers
      {} {} {username {:ALL {:run-as-user :ALL :tags :NOPASSWD}}})))
