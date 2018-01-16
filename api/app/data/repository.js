'use strict';

module.exports = {
    /** Describes an abstract collection of objects */    
    Repository: class Repository {
        add(item) {
            throw new Error('#add not implemented');
        }

        remove(item) {
            throw new Error('#remove not implemented');
        }

        replace(item) {
            throw new Error('#replace not implemented');
        }

        /** @param {Specification} specification */
        find(specification) {
            throw new Error('#find not implemented');
        }
    },
    NotFoundError: class NotFoundError extends Error {
        constructor() {
            super('object not found in repository')
        }
    }
};