(ns pallet.action.filesystem-test
  (:use clojure.test)
  (:require
   [pallet.action.directory :as directory]
   [pallet.action.exec-script :as exec-script]
   [pallet.action.filesystem :as filesystem]
   [pallet.build-actions :as build-actions]
   [pallet.stevedore :as stevedore]))

(deftest make-xfs-filesytem-test
  (is (= (first
          (build-actions/build-actions
           {}
           (exec-script/exec-checked-script
            "Format /dev/a as XFS"
            ("mkfs.xfs" -f "/dev/a"))))
         (first
          (build-actions/build-actions
           {}
           (filesystem/make-xfs-filesytem "/dev/a"))))))

(deftest mount-test
  (is (= (first
          (build-actions/build-actions
           {}
           (directory/directory "/mnt/a")
           (exec-script/exec-checked-script
            "Mount /dev/a at /mnt/a"
            (mount "/dev/a" (quoted "/mnt/a")))))
         (first
          (build-actions/build-actions
           {}
           (filesystem/mount "/dev/a" "/mnt/a"))))))