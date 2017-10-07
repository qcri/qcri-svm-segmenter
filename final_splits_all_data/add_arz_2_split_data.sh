#!/bin/sh

# Prepate Egyptian Data splits
# A Abdelali Last Update: Wed Apr 12 13:17:12 AST 2017
# QCRI-2017


for i in `ls -d lev*seg `; 
do 
	echo $i; 
	mkdir -p $i/splits_msa;
	cp  $i/splits/* $i/splits_msa/.;
	for j in `ls -1 $i/splits/*train*.trg`; 
	do 
		name=`echo $j | awk -F'/|.trg' '{print $3}'`
		echo cp arz.ldc.train $i/splits_msa/$name.trg 
		cp arz.ldc.train $i/splits_msa/$name.trg
		cat arz.ldc.train | awk '{gsub(/ل\+ال\+/,"لل");if(length($0)>2)gsub(/\+/,""); print $0}' > $i/splits_msa/$name.src
		
		for k in `seq 1 122`; do
			cat $j >> $i/splits_msa/$name.src
			cat $i/splits/$name.trg >> $i/splits_msa/$name.trg 
		done

	done;

done
