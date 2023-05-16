package com.bdwise.prometheus.client.builder;

public enum QueryBuilderType {
	RangeQuery {

		@Override
		@SuppressWarnings("unchecked")
		public RangeQueryBuilder newInstance(String prometheusUrl) {
			return new RangeQueryBuilder(prometheusUrl);
		}

	},
	InstantQuery {

		@Override
		@SuppressWarnings("unchecked")
		public InstantQueryBuilder newInstance(String prometheusUrl) {
			return new InstantQueryBuilder(prometheusUrl);
		}

	},
	SeriesMetadaQuery {

		@Override
		@SuppressWarnings("unchecked")
		public QueryBuilder newInstance(String prometheusUrl) {
			return new SeriesMetaQueryBuilder(prometheusUrl);
		}

	},
	LabelMetadaQuery {

		@Override
		@SuppressWarnings("unchecked")
		public QueryBuilder newInstance(String prometheusUrl) {
			return new LabelMetaQueryBuilder(prometheusUrl);
		}

	},

	TargetMetadaQuery {

		@Override
		@SuppressWarnings("unchecked")
		public QueryBuilder newInstance(String prometheusUrl) {
			return new TargetMetaQueryBuilder(prometheusUrl);
		}

	},

	AlertManagerMetadaQuery {

		@Override
		@SuppressWarnings("unchecked")
		public QueryBuilder newInstance(String prometheusUrl) {
			return new AlertManagerMetaQueryBuilder(prometheusUrl);
		}

	},

	StatusMetadaQuery {

		@Override
		@SuppressWarnings("unchecked")
		public QueryBuilder newInstance(String prometheusUrl) {
			return new StatusMetaQueryBuilder(prometheusUrl);
		}

	};

	public abstract <T extends QueryBuilder> T newInstance(String prometheusUrl);
}
