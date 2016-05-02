package org.apache.solr.update.processor;

public interface MMTextNormalizationParams {

	  
	  String TEXT_NORMALIZE = "normalize" ;
	  String FIELDS_PARAM = TEXT_NORMALIZE + ".fl";                 // Field list to detect from
	  
	  String LANGUAGE_ID = "langid";
	  String DOCID_PARAM =  LANGUAGE_ID + ".idField";
	  
	  String LANG_FIELD = LANGUAGE_ID + ".langField";            // Main language detected
	  String LANGS_FIELD = LANGUAGE_ID + ".langsField";          // All languages detected (multiValued)
	  String FALLBACK =  LANGUAGE_ID + ".fallback";              // Fallback lang code  
	  String FALLBACK_FIELDS =  LANGUAGE_ID + ".fallbackFields"; // Comma-sep list of fallback fields
	  String OVERWRITE  = LANGUAGE_ID + ".overwrite";            // Overwrite if existing language value in LANG_FIELD
	  String THRESHOLD  = LANGUAGE_ID + ".threshold";            // Detection threshold
	  String ENFORCE_SCHEMA =  LANGUAGE_ID + ".enforceSchema";   // Enforces that output fields exist in schema
	  String LANG_WHITELIST  = LANGUAGE_ID + ".whitelist";       // Allowed languages
	  String MAP_ENABLE =  LANGUAGE_ID + ".map";                 // Turns on or off the field mapping
	  String MAP_FL =  LANGUAGE_ID + ".map.fl";                  // Field list for mapping
	  String MAP_OVERWRITE =  LANGUAGE_ID + ".map.overwrite";    // Whether to overwrite existing fields
	  String MAP_KEEP_ORIG =  LANGUAGE_ID + ".map.keepOrig";     // Keep original field after mapping
	  String MAP_INDIVIDUAL =  LANGUAGE_ID + ".map.individual";  // Detect language per individual field
	  String MAP_INDIVIDUAL_FL =  LANGUAGE_ID + ".map.individual.fl";// Field list of fields to redetect language for
	  String MAP_LCMAP =  LANGUAGE_ID + ".map.lcmap";            // Enables mapping multiple langs to same output field
	  String MAP_PATTERN =  LANGUAGE_ID + ".map.pattern";        // RegEx pattern to match field name
	  String MAP_REPLACE =  LANGUAGE_ID + ".map.replace";        // Replace pattern

	  String DOCID_FIELD_DEFAULT = "id";
	  String DOCID_LANGFIELD_DEFAULT = null;
	  String DOCID_LANGSFIELD_DEFAULT = null;
	  String MAP_PATTERN_DEFAULT = "(.*)";
	  String MAP_REPLACE_DEFAULT = "$1_{lang}";

	  // TODO: This default threshold accepts even "uncertain" detections. 
	  // Increase &langid.threshold above 0.5 to return only certain detections
	  Double DOCID_THRESHOLD_DEFAULT = 0.5;
}
