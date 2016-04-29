@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

rem    web/ABC/STG_output2
rem    web/BMT/BMT_job_sample
rem    web/CLA/output2
rem    web/CLA2/output2
rem    web/EDW/EDW_output2
rem    web/INT/INT_output2
rem    set1/btl_export
rem    set2/output2
rem    set2_25/output2
rem    set3/output2
rem    web/DSExport/DSExport
rem    setGen1cluster/1clus_840
rem    setGen2clusters/2clus_805

for %%d in (
    comprehensiveCLA/1/output2_1
    comprehensiveCLA/2/output2_2
    comprehensiveCLA/3/output2_3
    comprehensiveCLA/23/output2_23
) do (
	for /r %%p in (_params_files\*) do (
		echo ===============================================================================
		echo ======== TESTING %%d
		echo ========   Parameters: %%~np
		echo ===============================================================================
		call java -jar ETLPattFind.jar ../Samples/%%d_red.xml -o ../Samples/%%d_red_%%~np_patterns.txt -p _params_files/%%~nxp -s ../Samples/%%d_red_%%~np.txt
	)
)

pause